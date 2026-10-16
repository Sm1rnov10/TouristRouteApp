package com.tourist.service;

import com.tourist.entity.Place;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class PlaceService {

    @PersistenceContext(unitName = "TouristRoutePU")
    private EntityManager em;

    @EJB
    private RouteService routeService;

    public List<Place> findAll() {
        return em.createQuery(
                "SELECT p FROM Place p ORDER BY p.id",
                Place.class
        ).getResultList();
    }

    public Place findById(Long id) {
        return em.find(Place.class, id);
    }

    public void create(Place place) {
        em.persist(place);
    }

    public void update(Place place) {
        em.merge(place);
    }

    public void delete(Long id) {
        Place place = em.find(Place.class, id);

        if (place == null) {
            return;
        }

        List<Long> routeIds = em.createQuery(
                        "SELECT rp.route.id FROM RoutePlace rp " +
                                "WHERE rp.place.id = :placeId",
                        Long.class
                )
                .setParameter("placeId", id)
                .getResultList();

        for (Long routeId : routeIds) {
            routeService.removePlace(routeId, id);
        }

        em.remove(place);
    }
}
