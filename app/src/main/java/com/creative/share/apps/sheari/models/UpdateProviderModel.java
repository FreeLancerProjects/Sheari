package com.creative.share.apps.sheari.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.creative.share.apps.sheari.R;

import java.io.Serializable;


public class UpdateProviderModel extends BaseObservable implements Serializable {

    private String name;
    private String email;
    private Uri image;
    private int region_id;



    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();


    public boolean isValid(Context context)
    {
        if (!TextUtils.isEmpty(name)&&
                !TextUtils.isEmpty(email)&&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()

        )
        {
            error_name.set(null);
            error_email.set(null);

            error_phone.set(null);

            return true;
        }else
        {

            if (name.isEmpty())
            {
                error_name.set(context.getString(R.string.field_req));
            }else
            {
                error_name.set(null);
            }

            if (email.isEmpty())
            {
                error_email.set(context.getString(R.string.field_req));

            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                error_email.set(context.getString(R.string.inv_email));
            }
            else
            {
                error_email.set(null);
            }


           /* if (phone.isEmpty())
            {
                error_phone.set(context.getString(R.string.field_req));
            }else
            {
                error_phone.set(null);
            }*/










            return false;
        }
    }


    public UpdateProviderModel() {
        //this.phone="";
        //notifyPropertyChanged(BR.phone);
        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);






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

    /*@Bindable
    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
        notifyPropertyChanged(BR.about_me);
    }*/

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    /*@Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }
*/

}
