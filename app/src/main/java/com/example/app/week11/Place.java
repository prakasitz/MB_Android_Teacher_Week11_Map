package com.example.app.week11;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private String name;
    private LatLng latLng;

    public Place(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}

