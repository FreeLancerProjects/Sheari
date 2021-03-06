package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class MessageDataModel implements Serializable {
    private boolean value;
    private String msg;
    private Data data;






    public class Data implements Serializable
    {
        private List<MessageModel> inbox;
        private Paginate paginate;

        public Paginate getPaginate() {
            return paginate;
        }

        public List<MessageModel> getInbox() {
            return inbox;
        }
    }

    public boolean isValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public class Paginate implements Serializable
    {
        private int count;
        private int total_pages;

        public int getCount() {
            return count;
        }

        public int getTotal_pages() {
            return total_pages;
        }
    }
}
