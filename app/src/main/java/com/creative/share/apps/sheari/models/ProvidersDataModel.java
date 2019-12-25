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
    public class ProviderModel implements Serializable
    {
        private int id;
        private String name;
        private String email;
        private String phone;
        private double rate;
        private boolean isOnline;
        private String image;
        private String gender;
        private String region_name;
        private String city_name;
        private String country_name;
        private String is_special;
        private String lat;
        private String lng;
        private List<ProviderSubCategory> sub_categories;

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

        public double getRate() {
            return rate;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public String getImage() {
            return image;
        }

        public String getGender() {
            return gender;
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

        public String getIs_special() {
            return is_special;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }

        public List<ProviderSubCategory> getSub_categories() {
            return sub_categories;
        }
    }

    public class ProviderSubCategory implements Serializable
    {

        private int id;
        private String ar_name;
        private String en_name;
        private String image;
        private String category_id;
        private Pivot pivot;

        public int getId() {
            return id;
        }

        public String getAr_name() {
            return ar_name;
        }

        public String getEn_name() {
            return en_name;
        }

        public String getImage() {
            return image;
        }

        public String getCategory_id() {
            return category_id;
        }

        public Pivot getPivot() {
            return pivot;
        }
    }

    public class Pivot implements Serializable
    {
        private String user_id;
        private String sub_category_id;

        public String getUser_id() {
            return user_id;
        }

        public String getSub_category_id() {
            return sub_category_id;
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
