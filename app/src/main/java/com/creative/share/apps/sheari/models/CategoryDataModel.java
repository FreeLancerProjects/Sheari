package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class CategoryDataModel implements Serializable {
    private boolean value;
    private String msg;
    private List<CategoryModel> data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public List<CategoryModel> getData() {
        return data;
    }


}
