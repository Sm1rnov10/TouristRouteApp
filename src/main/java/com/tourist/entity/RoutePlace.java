package com.tourist.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "route_places")
@IdClass(RoutePlaceId.class)
public class RoutePlace {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "visit_order", nullable = false)
    private int visitOrder;


    @Column(name = "visit_duration_minutes", nullable = false)
    private int visitDurationMinutes = 60;

    public RoutePlace(Route route, Place place,
                      int visitOrder, int visitDurationMinutes) {
        this.route = route;
        this.place = place;
        this.visitOrder = visitOrder;
        this.visitDurationMinutes = visitDurationMinutes;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getVisitOrder() {
        return visitOrder;
    }

    public void setVisitOrder(int visitOrder) {
        this.visitOrder = visitOrder;
    }

    public int getVisitDurationMinutes() {
        return visitDurationMinutes;
    }

    public void setVisitDurationMinutes(int visitDurationMinutes) {
        this.visitDurationMinutes = visitDurationMinutes;
    }

    public RoutePlace() {
    }
}
