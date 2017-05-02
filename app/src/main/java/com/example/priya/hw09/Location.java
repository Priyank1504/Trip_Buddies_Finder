package com.example.priya.hw09;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by priyank Verma
 */
public class Location implements Serializable {
    String locName;
    Double lat, lng;
    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
