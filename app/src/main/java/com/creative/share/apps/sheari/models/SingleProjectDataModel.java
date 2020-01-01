package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class SingleProjectDataModel implements Serializable {

    private boolean value;
    private String msg;
    private ProjectModel data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public ProjectModel getData() {
        return data;
    }
}
