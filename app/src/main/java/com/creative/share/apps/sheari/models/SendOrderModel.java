package com.creative.share.apps.sheari.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.creative.share.apps.sheari.R;

import java.io.Serializable;

public class SendOrderModel extends BaseObservable implements Serializable {

    private String title;
    private String details;
    private String date;
    private String time;
    private String important;
    private String duration;
    private String budget;
    private String description;

    public ObservableField<String> error_title = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();
    public ObservableField<String> error_date = new ObservableField<>();
    public ObservableField<String> error_time = new ObservableField<>();
    public ObservableField<String> error_important = new ObservableField<>();
    public ObservableField<String> error_duration = new ObservableField<>();
    public ObservableField<String> error_budget = new ObservableField<>();
    //public ObservableField<String> error_description = new ObservableField<>();


    public SendOrderModel() {
        this.title = "";
        this.details = "";
        this.date = "";
        this.time = "";
        this.important = "";
        this.duration = "";
        this.budget = "";
        //this.description = "";


    }

    public boolean isDataValid(Context context) {

        if (!title.isEmpty() &&
                !details.isEmpty() &&
                !date.isEmpty() &&
                !time.isEmpty() &&
                !important.isEmpty() &&
                !duration.isEmpty() &&
                !budget.isEmpty()) {

            error_title.set(null);
            error_budget.set(null);
            error_date.set(null);
            //error_description.set(null);
            error_details.set(null);
            error_duration.set(null);
            error_time.set(null);
            error_important.set(null);

            return true;

        } else {


            if (title.isEmpty())
            {
                error_title.set(context.getString(R.string.field_req));
            }else
            {
                error_title.set(null);
            }


            if (details.isEmpty())
            {
                error_details.set(context.getString(R.string.field_req));
            }else
            {
                error_details.set(null);
            }


            if (important.isEmpty())
            {
                error_important.set(context.getString(R.string.field_req));
            }else
            {
                error_important.set(null);
            }


            if (duration.isEmpty())
            {
                error_duration.set(context.getString(R.string.field_req));
            }else
            {
                error_duration.set(null);
            }


            if (budget.isEmpty())
            {
                error_budget.set(context.getString(R.string.field_req));
            }else
            {
                error_budget.set(null);
            }


            if (date.isEmpty())
            {
                Toast.makeText(context, R.string.ch_date, Toast.LENGTH_SHORT).show();
            }

            if (time.isEmpty())
            {
                Toast.makeText(context, R.string.ch_time, Toast.LENGTH_SHORT).show();
            }

            if (important.isEmpty())
            {
                Toast.makeText(context, R.string.ch_important, Toast.LENGTH_SHORT).show();
            }

        }

        return false;
    }


    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Bindable
    public String getImportant() {
        return important;
    }

    public void setImportant(String important) {
        this.important = important;
        notifyPropertyChanged(BR.important);
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);

    }

    @Bindable
    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
        notifyPropertyChanged(BR.budget);

    }

    /*@Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.budget);

    }*/
}
