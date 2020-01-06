package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class ResponseActiveUser implements Serializable {

    private boolean value;
    private String msg;
    ////////////////////////////////
    private String message;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public String getMessage() {
        return message;
    }
}
