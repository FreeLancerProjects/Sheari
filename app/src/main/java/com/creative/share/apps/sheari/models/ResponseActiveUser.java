package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class ResponseActiveUser implements Serializable {

    private boolean value;
    private String msg;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }
}
