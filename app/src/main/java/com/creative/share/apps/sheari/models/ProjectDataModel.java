package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class ProjectDataModel implements Serializable {
    private boolean value;
    private String msg;
    private List<ProjectModel> data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public List<ProjectModel> getData() {
        return data;
    }
}
