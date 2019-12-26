package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class UserModel implements Serializable {

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
        private boolean isOnline;
        private int id;
        private String name;
        private String identity;
        private String email;
        private String role;
        private String bio;
        private String image;
        private String region_name;
        private String city_name;
        private String country_name;
        private String lng;
        private String lat;
        private double rate;
        private String delivery;
        private String emp_no;
        private String creation_year;
        private String commerical_no;
        private String discount;
        private String nationality;
        private String gender;
        private String experience_years;
        private String token;


        public boolean isOnline() {
            return isOnline;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIdentity() {
            return identity;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getBio() {
            return bio;
        }

        public String getImage() {
            return image;
        }

        public String getRegion_name() {
            return region_name;
        }

        public String getCity_name() {
            return city_name;
        }

        public String getCountry_name() {
            return country_name;
        }

        public String getLng() {
            return lng;
        }

        public String getLat() {
            return lat;
        }

        public double getRate() {
            return rate;
        }

        public String getDelivery() {
            return delivery;
        }

        public String getEmp_no() {
            return emp_no;
        }

        public String getCreation_year() {
            return creation_year;
        }

        public String getCommerical_no() {
            return commerical_no;
        }

        public String getDiscount() {
            return discount;
        }

        public String getNationality() {
            return nationality;
        }

        public String getGender() {
            return gender;
        }

        public String getExperience_years() {
            return experience_years;
        }

        public String getToken() {
            return token;
        }
    }

}
