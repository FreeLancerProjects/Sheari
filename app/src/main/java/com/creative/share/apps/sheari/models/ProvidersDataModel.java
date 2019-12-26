package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class ProvidersDataModel implements Serializable {
    private boolean value;
    private String msg;
    private Data data;

    public boolean isValue() {
        return value;
    }

    public Data getData() {
        return data;
    }



    public String getMsg() {
        return msg;
    }

    public class Data implements Serializable
    {
        private List<ProviderModel> providers;
        private Paginate paginate;

        public List<ProviderModel> getProviders() {
            return providers;
        }

        public Paginate getPaginate() {
            return paginate;
        }
    }




    public class Paginate implements Serializable
    {
        private int current_page;
        private int total_pages;

        public int getTotal_pages() {
            return total_pages;
        }

        public int getCurrent_page() {
            return current_page;
        }
    }
}
