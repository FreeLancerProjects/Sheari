package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class PayPalLinkModel implements Serializable {
    private boolean value;
    private String data;

    public boolean isValue() {
        return value;
    }

    public String getData() {
        return data;
    }
}
