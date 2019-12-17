package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    private int id;
    private String name;
    private String image;

    public CategoryModel() {
    }

    public CategoryModel(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
