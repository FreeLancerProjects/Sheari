package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

public class CommentDataModel implements Serializable {

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

    public class Data implements Serializable
    {
        private UserModel.Data user;

        private List<Comment> comments;

        public List<Comment> getComments() {
            return comments;
        }

        public class Comment implements Serializable
        {
            private int id;
            private String ads_id;
            private String user_id;
            private String comment;

            public int getId() {
                return id;
            }

            public String getAds_id() {
                return ads_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public String getComment() {
                return comment;
            }
        }


        public UserModel.Data getUser() {
            return user;
        }
    }


}
