package com.creative.share.apps.sheari.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.creative.share.apps.sheari.R;

import java.io.Serializable;


public class CreateProjectModel extends BaseObservable implements Serializable {

    private String title;
    private String description;
    private Uri image;



    public ObservableField<String> error_title = new ObservableField<>();
    public ObservableField<String> error_description = new ObservableField<>();


    public boolean isValidCreate(Context context)
    {
        Log.e("title",title+"__");
        Log.e("des",description+"__");
        Log.e("img",image+"__");

        if (!TextUtils.isEmpty(title)&&
                !TextUtils.isEmpty(description)&&
                image!=null


        )
        {
            error_title.set(null);
            error_description.set(null);


            return true;
        }else
        {

            if (title.isEmpty())
            {
                error_title.set(context.getString(R.string.field_req));
            }else
            {
                error_title.set(null);
            }

            if (description.isEmpty())
            {
                error_description.set(context.getString(R.string.field_req));

            }
            else
            {
                error_description.set(null);
            }


            if(image==null)
            {
                Toast.makeText(context, context.getString(R.string.ch_pro_img), Toast.LENGTH_SHORT).show();
            }





            return false;
        }
    }

    public boolean isValidUpdate(Context context)
    {
        if (!TextUtils.isEmpty(title)&&
                !TextUtils.isEmpty(description)


        )
        {
            error_title.set(null);
            error_description.set(null);


            return true;
        }else
        {

            if (title.isEmpty())
            {
                error_title.set(context.getString(R.string.field_req));
            }else
            {
                error_title.set(null);
            }

            if (description.isEmpty())
            {
                error_description.set(context.getString(R.string.field_req));

            }
            else
            {
                error_description.set(null);
            }






            return false;
        }
    }


    public CreateProjectModel() {
        this.title="";
        notifyPropertyChanged(BR.title);
        this.description="";
        notifyPropertyChanged(BR.description);







    }


    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public String getDescription() {
        return description;
    }


    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);

    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);

    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
