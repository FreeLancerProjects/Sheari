package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class SingleMessageDataModel implements Serializable {

    //send message
    private boolean status;
    ///////////////////////////////////////
    //get all message

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

    public boolean isStatus() {
        return status;
    }
}
