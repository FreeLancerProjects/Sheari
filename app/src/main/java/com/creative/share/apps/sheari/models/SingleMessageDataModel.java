package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class SingleMessageDataModel implements Serializable {
    private boolean value;
    private String msg;
    private MessageModel data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public MessageModel getData() {
        return data;
    }
}
