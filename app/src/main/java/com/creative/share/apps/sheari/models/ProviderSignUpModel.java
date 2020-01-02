package com.creative.share.apps.sheari.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.creative.share.apps.sheari.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProviderSignUpModel extends BaseObservable implements Serializable {

    private int type;
    private String company_type;
    private String name;
    private int country_id;
    private int city_id;
    private String email;
    private String phone_code;
    private String phone;
    private String password;
    private String re_password;
    private Uri image_uri;
    private String about_me;
    private int service;
    private String from_emp;
    private String to_emp;
    private String year;

    private String address;
    private String commercial;
    private int ad_dept_id;
    private int region_id;
    private List<Integer> sub_dept_ids = new ArrayList<>();

    private double lat;
    private double lng;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();
    public ObservableField<String> error_re_password = new ObservableField<>();
    public ObservableField<String> error_about = new ObservableField<>();
    public ObservableField<String> error_from_emp = new ObservableField<>();
    public ObservableField<String> error_to_emp = new ObservableField<>();
    public ObservableField<String> error_year = new ObservableField<>();

    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_commercial = new ObservableField<>();


    public boolean step1IsValid(Context context) {
        if (type != 0) {
            return true;
        } else {
            Toast.makeText(context, R.string.ch_type, Toast.LENGTH_SHORT).show();
            return false;

        }

    }

    public boolean step2IsValid(Context context) {
        if (type == 1) {
            if (!name.isEmpty()&&region_id!=0
            ) {
                error_name.set(null);
                return true;
            } else {
                if (TextUtils.isEmpty(name)) {
                    error_name.set(context.getString(R.string.field_req));
                } else {
                    error_name.set(null);
                }


                if (region_id == 0) {
                    Toast.makeText(context, R.string.ch_region, Toast.LENGTH_SHORT).show();
                }
               /*

                if (city_id == 0) {
                    Toast.makeText(context, R.string.ch_city, Toast.LENGTH_SHORT).show();
                }*/


                return false;
            }
        } else {
            if (!company_type.isEmpty() &&
                    !name.isEmpty()
            ) {
                error_name.set(null);
                return true;
            } else {
                if (TextUtils.isEmpty(name)) {
                    error_name.set(context.getString(R.string.field_req));
                } else {
                    error_name.set(null);
                }

                if (company_type.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.ch_type), Toast.LENGTH_SHORT).show();
                }

                /*if (country_id == 0) {
                    Toast.makeText(context, R.string.ch_country, Toast.LENGTH_SHORT).show();
                }

                if (city_id == 0) {
                    Toast.makeText(context, R.string.ch_city, Toast.LENGTH_SHORT).show();
                }*/


                return false;
            }
        }

    }

    public boolean step3IsValid(Context context) {
        if (!phone_code.isEmpty() &&
                !phone.isEmpty() &&
                !email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length() > 5 &&
                password.equals(re_password)
        ) {
            error_phone_code.set(null);
            error_phone.set(null);
            error_email.set(null);
            error_password.set(null);
            error_re_password.set(null);
            Log.e("1", "1");

            return true;
        } else {

            Log.e("2", "2");


            if (TextUtils.isEmpty(phone_code)) {
                error_phone_code.set(context.getString(R.string.field_req));
            } else {
                error_phone_code.set(null);
            }


            if (TextUtils.isEmpty(phone)) {
                error_phone.set(context.getString(R.string.field_req));
            } else {
                error_phone.set(null);
            }


            if (email.isEmpty()) {
                error_email.set(context.getString(R.string.field_req));

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                error_email.set(context.getString(R.string.inv_email));
            } else {
                error_email.set(null);
            }


            if (password.isEmpty()) {
                error_password.set(context.getString(R.string.field_req));
            } else if (password.length() < 6) {
                error_password.set(context.getString(R.string.pass_short));
            } else if (!password.equals(re_password)) {
                error_re_password.set(context.getString(R.string.re_pass_not_match));
            } else {
                error_password.set(null);
                error_re_password.set(null);
            }


            return false;
        }
    }

    public boolean step4IsValid(Context context) {
        if (type == 1) {

            if (!about_me.isEmpty() &&
                    service != 0 &&
                    !address.isEmpty() &&
                    ad_dept_id !=0 &&
                    sub_dept_ids.size()>0


            ) {
                error_about.set(null);
                error_from_emp.set(null);
                error_to_emp.set(null);
                error_year.set(null);
                error_commercial.set(null);
                error_address.set(null);

                return true;
            } else {
                if (TextUtils.isEmpty(about_me)) {
                    error_about.set(context.getString(R.string.field_req));
                } else {
                    error_about.set(null);
                }







                if (TextUtils.isEmpty(address)) {
                    error_address.set(context.getString(R.string.field_req));
                } else {
                    error_address.set(null);
                }

                if (service == 0) {
                    Toast.makeText(context, R.string.ch_serv, Toast.LENGTH_SHORT).show();
                }
                if (ad_dept_id ==0) {
                    Toast.makeText(context, R.string.ch_ad_dept, Toast.LENGTH_SHORT).show();
                }

                if (sub_dept_ids.size()==0) {
                    Toast.makeText(context, R.string.ch_sub_dept, Toast.LENGTH_SHORT).show();
                }


                return false;
            }
        } else {

            if (!about_me.isEmpty() &&
                    service != 0 &&
                    !from_emp.isEmpty() &&
                    !to_emp.isEmpty() &&
                    !year.isEmpty() &&
                    !commercial.isEmpty() &&
                    !address.isEmpty() &&
                    ad_dept_id !=0 &&
                    sub_dept_ids.size()>0


            ) {
                error_about.set(null);
                error_from_emp.set(null);
                error_to_emp.set(null);
                error_year.set(null);
                error_commercial.set(null);
                error_address.set(null);

                return true;
            } else {
                if (TextUtils.isEmpty(about_me)) {
                    error_about.set(context.getString(R.string.field_req));
                } else {
                    error_about.set(null);
                }


                if (TextUtils.isEmpty(from_emp)) {
                    error_from_emp.set(context.getString(R.string.field_req));
                } else {
                    error_from_emp.set(null);
                }

                if (TextUtils.isEmpty(year)) {
                    error_year.set(context.getString(R.string.field_req));
                } else {
                    error_year.set(null);
                }

                if (TextUtils.isEmpty(commercial)) {
                    error_commercial.set(context.getString(R.string.field_req));
                } else {
                    error_commercial.set(null);
                }

                if (TextUtils.isEmpty(to_emp)) {
                    error_to_emp.set(context.getString(R.string.field_req));
                } else {
                    error_to_emp.set(null);
                }



                if (TextUtils.isEmpty(address)) {
                    error_address.set(context.getString(R.string.field_req));
                } else {
                    error_address.set(null);
                }

                if (service == 0) {
                    Toast.makeText(context, R.string.ch_serv, Toast.LENGTH_SHORT).show();
                }
                if (ad_dept_id ==0) {
                    Toast.makeText(context, context.getString(R.string.ch_dept), Toast.LENGTH_SHORT).show();
                }

                if (sub_dept_ids.size()==0) {
                    Toast.makeText(context, R.string.ch_sub_dept, Toast.LENGTH_SHORT).show();
                }


                return false;
            }
        }

    }


    public ProviderSignUpModel() {
        this.phone_code = "";
        notifyPropertyChanged(BR.phone_code);
        this.phone = "";
        notifyPropertyChanged(BR.phone);
        this.password = "";
        notifyPropertyChanged(BR.password);
        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);
        this.re_password = "";
        notifyPropertyChanged(BR.re_password);
        this.type = 0;
        this.company_type ="";
        this.city_id = 0;
        this.country_id = 0;
        this.service = 0;
        this.year = "";
        notifyPropertyChanged(BR.year);
        this.image_uri = null;
        this.about_me = "";
        notifyPropertyChanged(BR.about_me);
        this.from_emp = "";
        notifyPropertyChanged(BR.from_emp);
        this.to_emp = "";
        notifyPropertyChanged(BR.to_emp);
        this.address = "";
        notifyPropertyChanged(BR.address);
        this.lat = 0.0;
        this.lng = 0.0;
        this.ad_dept_id = 0;
        this.region_id = 0;


    }


    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);

    }

    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public Uri getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(Uri image_uri) {
        this.image_uri = image_uri;
    }

    @Bindable
    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
        notifyPropertyChanged(BR.about_me);
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    @Bindable
    public String getFrom_emp() {
        return from_emp;
    }

    public void setFrom_emp(String from_emp) {
        this.from_emp = from_emp;
        notifyPropertyChanged(BR.from_emp);
    }

    @Bindable
    public String getTo_emp() {
        return to_emp;
    }

    public void setTo_emp(String to_emp) {
        this.to_emp = to_emp;
        notifyPropertyChanged(BR.to_emp);

    }

    @Bindable
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }


    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Bindable
    public String getCommercial() {
        return commercial;
    }

    public void setCommercial(String commercial) {
        this.commercial = commercial;
        notifyPropertyChanged(BR.commercial);
    }

    @Bindable
    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
        notifyPropertyChanged(BR.re_password);

    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public int getAd_dept_id() {
        return ad_dept_id;
    }

    public void setAd_dept_id(int ad_dept_id) {
        this.ad_dept_id = ad_dept_id;
    }

    public List<Integer> getSub_dept_ids() {
        return sub_dept_ids;
    }

    public void setSub_dept_ids(List<Integer> sub_dept_ids) {
        this.sub_dept_ids = sub_dept_ids;
    }
}
