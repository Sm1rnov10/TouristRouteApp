package com.tourist.entity;

import java.io.Serializable;
import java.util.Objects;

public class RoutePlaceId implements Serializable {

    private Long route;
    private Long place;

    public RoutePlaceId() {
    }

    public RoutePlaceId(Long route, Long place) {
        this.route = route;
        this.place = place;
    }

    public Long getRoute() {
        return route;
    }

    public void setRoute(Long route) {
        this.route = route;
    }

    public Long getPlace() {
        return place;
    }

    public void setPlace(Long place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutePlaceId)) return false;

        RoutePlaceId that = (RoutePlaceId) o;
        return Objects.equals(route, that.route)
                && Objects.equals(place, that.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, place);
    }
}
