package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {
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
        private Paginate paginate;
        private List<NotificationModel> notifications;

        public List<NotificationModel> getNotifications() {
            return notifications;
        }

        public Paginate getPaginate() {
            return paginate;
        }




        public class NotificationModel implements Serializable
        {
            private int id;
            private String type;
            private String order_id;
            private String title;
            private String user_name;
            private CreateAt created_at;




            public int getId() {
                return id;
            }

            public String getType() {
                return type;
            }

            public String getOrder_id() {
                return order_id;
            }

            public String getTitle() {
                return title;
            }

            public String getUser_name() {
                return user_name;
            }

            public CreateAt getCreated_at() {
                return created_at;
            }
        }

        public class CreateAt implements Serializable
        {
            private String date;

            public String getDate() {
                return date;
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
