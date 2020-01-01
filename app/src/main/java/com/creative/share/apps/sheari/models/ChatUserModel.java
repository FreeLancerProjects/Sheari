package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class ChatUserModel implements Serializable {

    private String name;
    private String image;
    private int id;
    private String phone;
    private int order_id;

    public ChatUserModel(String name, String image, int id, String phone, int order_id) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.phone = phone;
        this.order_id = order_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public int getOrder_id() {
        return order_id;
    }
}
