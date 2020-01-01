package com.creative.share.apps.sheari.models;

import java.io.Serializable;
import java.util.List;

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
        private String phone;
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
        private String is_special;
        private String emp_no;
        private String creation_year;
        private String commerical_no;
        private String discount;
        private String nationality;
        private String gender;
        private String experience_years;
        private String token;
        private String is_verified;
        private List<SubCategory> sub_categories;

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

        public String getPhone() {
            return phone;
        }

        public String getIs_special() {
            return is_special;
        }

        public List<SubCategory> getSub_categories() {
            return sub_categories;
        }

        public String getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(String is_verified) {
            this.is_verified = is_verified;
        }

        public class SubCategory implements Serializable
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
        }
    }

}
