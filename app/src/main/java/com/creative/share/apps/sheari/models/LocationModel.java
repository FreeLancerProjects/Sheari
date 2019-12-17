package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private int id;
    private String name;


    public LocationModel() {
    }

    public LocationModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
