package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class TermsModel implements Serializable {

    private boolean value;
    private String msg;
    private String data;

    public boolean isValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
