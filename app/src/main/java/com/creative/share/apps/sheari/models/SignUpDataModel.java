package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class SignUpDataModel implements Serializable {
    private boolean value;
    private String msg;
    private UserModel userModel;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public UserModel getUserModel() {
        return userModel;
    }
}
