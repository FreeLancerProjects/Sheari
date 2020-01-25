package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class MessageModel implements Serializable {

    private int id;
    private String order_id;
    private String order_status;
    private String sender_id;
    private String receiver_id;
    private String message;
    private Sender sender;
    private Sender receiver;


    public MessageModel() {
    }

    public MessageModel(int id, String order_id, String order_status, String sender_id, String receiver_id, String message, Sender sender, Sender receiver) {
        this.id = id;
        this.order_id = order_id;
        this.order_status = order_status;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public Sender getSender() {
        return sender;
    }

    public Sender getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public class Sender implements Serializable
    {
        private int id;
        private String name;
        private String email;
        private String phone;
        private String image;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getImage() {
            return image;
        }
    }

}
