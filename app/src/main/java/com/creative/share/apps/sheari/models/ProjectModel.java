package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class ProjectModel implements Serializable {

    private int id;
    private int user_id;
    private String title;
    private String file;
    private String description;


    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getFile() {
        return file;
    }

    public String getDescription() {
        return description;
    }
}
