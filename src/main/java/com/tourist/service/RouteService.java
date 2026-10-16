package com.tourist.service;

import com.tourist.entity.Place;
import com.tourist.entity.Route;
import com.tourist.entity.RoutePlace;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class RouteService {

    @PersistenceContext(unitName = "TouristRoutePU")
    private EntityManager em;

    public List<Route> findAll() {
        return em.createQuery(
                "SELECT r FROM Route r ORDER BY r.id",
                Route.class
        ).getResultList();
    }

    public List<RoutePlace> findRoutePlaces(Long routeId) {
        return em.createQuery(
                        "SELECT rp FROM RoutePlace rp " +
                                "JOIN FETCH rp.place " +
                                "WHERE rp.route.id = :routeId " +
                                "ORDER BY rp.visitOrder ASC",
                        RoutePlace.class
                )
                .setParameter("routeId", routeId)
                .getResultList();
    }

    public Route findById(Long id) {
        return em.find(Route.class, id);
    }

    public void create(Route route) {
        route.setDurationMinutes(0);
        em.persist(route);
    }

    public void update(Route route) {
        Route existing = em.find(Route.class, route.getId());

        if (existing == null) {
            throw new IllegalArgumentException("Маршрут не найден");
        }

        existing.setName(route.getName());
        existing.setDescription(route.getDescription());
    }

    public void delete(Long routeId) {
        Route route = em.find(Route.class, routeId);

        if (route == null) {
            return;
        }

        em.createQuery(
                        "DELETE FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId"
                )
                .setParameter("routeId", routeId)
                .executeUpdate();

        em.clear();

        route = em.find(Route.class, routeId);
        em.remove(route);
    }

    public void addPlace(Long routeId, Long placeId, int minutes) {

        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "Продолжительность должна быть положительной"
            );
        }

        Route route = em.find(Route.class, routeId);
        Place place = em.find(Place.class, placeId);

        if (route == null || place == null) {
            throw new IllegalArgumentException(
                    "Маршрут или место не найдено"
            );
        }

        Long count = em.createQuery(
                        "SELECT COUNT(rp) FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId " +
                                "AND rp.place.id = :placeId",
                        Long.class
                )
                .setParameter("routeId", routeId)
                .setParameter("placeId", placeId)
                .getSingleResult();

        if (count > 0) {
            throw new IllegalArgumentException(
                    "Место уже добавлено в маршрут"
            );
        }

        Integer maxOrder = em.createQuery(
                        "SELECT MAX(rp.visitOrder) FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId",
                        Integer.class
                )
                .setParameter("routeId", routeId)
                .getSingleResult();

        int nextOrder = (maxOrder == null) ? 1 : maxOrder + 1;

        RoutePlace routePlace =
                new RoutePlace(route, place, nextOrder, minutes);

        em.persist(routePlace);
        em.flush();

        recalculateDuration(routeId);
    }

    public void removePlace(Long routeId, Long placeId) {

        RoutePlace routePlace = em.createQuery(
                        "SELECT rp FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId " +
                                "AND rp.place.id = :placeId",
                        RoutePlace.class
                )
                .setParameter("routeId", routeId)
                .setParameter("placeId", placeId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (routePlace == null) {
            return;
        }

        em.remove(routePlace);
        em.flush();

        List<RoutePlace> remaining = em.createQuery(
                        "SELECT rp FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId " +
                                "ORDER BY rp.visitOrder ASC",
                        RoutePlace.class
                )
                .setParameter("routeId", routeId)
                .getResultList();

        int maxOrder = 0;

        for (RoutePlace rp : remaining) {
            maxOrder = Math.max(maxOrder, rp.getVisitOrder());
        }

        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setVisitOrder(maxOrder + i + 1);
        }

        em.flush();

        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setVisitOrder(i + 1);
        }

        em.flush();

        recalculateDuration(routeId);
    }

    public void recalculateDuration(Long routeId) {

        Route route = em.find(Route.class, routeId);

        if (route == null) {
            return;
        }

        em.flush();

        Long total = em.createQuery(
                        "SELECT SUM(rp.visitDurationMinutes) " +
                                "FROM RoutePlace rp " +
                                "WHERE rp.route.id = :routeId",
                        Long.class
                )
                .setParameter("routeId", routeId)
                .getSingleResult();

        route.setDurationMinutes(
                total == null ? 0 : Math.toIntExact(total)
        );
    }
}
