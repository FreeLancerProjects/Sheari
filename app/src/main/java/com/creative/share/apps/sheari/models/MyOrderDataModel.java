package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class MyOrderDataModel implements Serializable {
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
        private List<OrderModel> orders;
        private Paginate paginate;

        public List<OrderModel> getOrderModelList() {
            return orders;
        }

        public Paginate getPaginate() {
            return paginate;
        }




        public class OrderModel implements Serializable
        {
            private int id;
            private int user_id;
            private int provider_id;
            private String title;
            private String name;
            private String client_image;
            private String provider_image;
            private String details;
            private String status;
            private String full_date;
            private String category_id;

            private String important;
            private String expected_time;
            private String expected_money;
            private String attachment;
            private String lng;
            private String lat;
            private String rate;


            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getName() {
                return name;
            }

            public String getClient_image() {
                return client_image;
            }

            public String getProvider_image() {
                return provider_image;
            }

            public String getDetails() {
                return details;
            }

            public String getStatus() {
                return status;
            }

            public String getFull_date() {
                return full_date;
            }

            public String getCategory_id() {
                return category_id;
            }

            public String getImportant() {
                return important;
            }

            public String getExpected_time() {
                return expected_time;
            }

            public String getExpected_money() {
                return expected_money;
            }

            public String getAttachment() {
                return attachment;
            }

            public String getLng() {
                return lng;
            }

            public String getLat() {
                return lat;
            }

            public String getRate() {
                return rate;
            }

            public int getUser_id() {
                return user_id;
            }

            public int getProvider_id() {
                return provider_id;
            }
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
