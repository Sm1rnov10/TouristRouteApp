package com.tourist.web;

import com.tourist.entity.Place;
import com.tourist.entity.Route;
import com.tourist.entity.RoutePlace;
import com.tourist.service.PlaceService;
import com.tourist.service.RouteService;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("routeBean")
@SessionScoped
public class RouteBean implements Serializable {

    @EJB
    private RouteService routeService;

    @EJB
    private PlaceService placeService;

    private Route route = new Route();

    private Long selectedRouteId;
    private Long selectedPlaceId;

    private int visitMinutes = 60;
    private boolean editing = false;

    public List<Route> getRoutes() {
        return routeService.findAll();
    }

    public List<Place> getAvailablePlaces() {
        return placeService.findAll();
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public boolean isEditing() {
        return editing;
    }

    public Long getSelectedRouteId() {
        return selectedRouteId;
    }

    public void setSelectedRouteId(Long selectedRouteId) {
        this.selectedRouteId = selectedRouteId;
    }

    public Long getSelectedPlaceId() {
        return selectedPlaceId;
    }

    public void setSelectedPlaceId(Long selectedPlaceId) {
        this.selectedPlaceId = selectedPlaceId;
    }

    public int getVisitMinutes() {
        return visitMinutes;
    }

    public void setVisitMinutes(int visitMinutes) {
        this.visitMinutes = visitMinutes;
    }

    public void prepareCreate() {
        route = new Route();
        editing = false;
    }

    public void prepareEdit(Long id) {
        Route existing = routeService.findById(id);

        if (existing != null) {
            route = existing;
            editing = true;
        }
    }

    public String save() {
        if (editing) {
            routeService.update(route);
        } else {
            routeService.create(route);
        }

        route = new Route();
        editing = false;

        return "routes?faces-redirect=true";
    }

    public String delete(Long id) {
        routeService.delete(id);

        if (id.equals(selectedRouteId)) {
            selectedRouteId = null;
        }

        return "routes?faces-redirect=true";
    }

    public void selectRoute(Long id) {
        selectedRouteId = id;
        selectedPlaceId = null;
        visitMinutes = 60;
    }

    public Route getSelectedRoute() {
        if (selectedRouteId == null) {
            return null;
        }

        return routeService.findById(selectedRouteId);
    }

    public List<RoutePlace> getRoutePlaces() {
        if (selectedRouteId == null) {
            return List.of();
        }

        return routeService.findRoutePlaces(selectedRouteId);
    }

    public String addPlace() {
        if (selectedRouteId != null && selectedPlaceId != null) {
            routeService.addPlace(
                    selectedRouteId,
                    selectedPlaceId,
                    visitMinutes
            );
        }

        selectedPlaceId = null;
        visitMinutes = 60;

        return "routes?faces-redirect=true";
    }

    public String removePlace(Long placeId) {
        if (selectedRouteId != null) {
            routeService.removePlace(selectedRouteId, placeId);
        }

        return "routes?faces-redirect=true";
    }
}
