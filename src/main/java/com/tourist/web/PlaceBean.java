package com.tourist.web;

import com.tourist.entity.Place;
import com.tourist.service.PlaceService;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("placeBean")
@SessionScoped
public class PlaceBean implements Serializable {

    @EJB
    private PlaceService placeService;

    private Place place = new Place();

    private boolean editing = false;

    public List<Place> getPlaces() {
        return placeService.findAll();
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public boolean isEditing() {
        return editing;
    }

    public void prepareCreate() {
        place = new Place();
        editing = false;
    }

    public void prepareEdit(Long id) {
        Place existing = placeService.findById(id);

        if (existing != null) {
            place = existing;
            editing = true;
        }
    }

    public String save() {
        if (editing) {
            placeService.update(place);
        } else {
            placeService.create(place);
        }

        place = new Place();
        editing = false;

        return "places?faces-redirect=true";
    }

    public String delete(Long id) {
        placeService.delete(id);
        return "places?faces-redirect=true";
    }
}
