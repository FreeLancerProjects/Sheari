package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class SliderDataModel implements Serializable {
    private boolean value;
    private String msg;
    private List<SliderModel> data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public List<SliderModel> getData() {
        return data;
    }

    public class SliderModel implements Serializable
    {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
