package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class CommentRespons implements Serializable {
    private boolean value;
    private String msg="";
    private Data data;

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable
    {
        private int id;
        private int user_id;
        private int ads_id;
        private String comment;

        public int getId() {
            return id;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getAds_id() {
            return ads_id;
        }

        public String getComment() {
            return comment;
        }
    }


}
