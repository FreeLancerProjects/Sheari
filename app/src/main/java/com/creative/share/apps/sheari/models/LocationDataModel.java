package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class LocationDataModel implements Serializable {
    private boolean value;
    private String msg;
    private List<LocationModel> data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public List<LocationModel> getData() {
        return data;
    }


}
