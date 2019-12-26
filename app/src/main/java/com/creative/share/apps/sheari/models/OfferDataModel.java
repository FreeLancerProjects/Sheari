package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class OfferDataModel implements Serializable {
    private boolean value;
    private String msg;
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

        private List<Provider> providers;

        private Paginate paginate;

        public List<Provider> getProviders() {
            return providers;
        }

        public Paginate getPaginate() {
            return paginate;
        }

        public static class  Provider implements Serializable
        {
            private int id;
            private String title;
            private String time_count;
            private String attachment;
            private String views;
            private String description;
            private String image;
            private ProviderModel user;

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getTime_count() {
                return time_count;
            }

            public String getAttachment() {
                return attachment;
            }

            public String getViews() {
                return views;
            }

            public String getDescription() {
                return description;
            }

            public String getImage() {
                return image;
            }

            public ProviderModel getUser() {
                return user;
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
}
