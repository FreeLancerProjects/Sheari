package com.creative.share.apps.sheari.ui_general_method;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UI_General_Method {

    @BindingAdapter("error")
    public static void setErrorUi(View view, String error) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setError(error);

        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setError(error);

        }
    }


    @BindingAdapter("image")
    public static void DisplayImage(View view, String url) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (url != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse(url)).fit().into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (url != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse(url)).fit().into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (url != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse(url)).fit().into(imageView);
            }
        }

    }

    @BindingAdapter("subCategoryImage")
    public static void DisplaySubCategotyImage(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse("http://sheari.net/storage/"+endPoint)).fit().into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse("http://sheari.net/storage/"+endPoint)).fit().into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.with(imageView.getContext()).load(Uri.parse("http://sheari.net/storage/"+endPoint)).fit().into(imageView);
            }
        }

    }


    @BindingAdapter({"date"})
    public static void displayTime(TextView textView, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(time * 1000));
        textView.setText(sTime);
    }

    @BindingAdapter("rate")
    public static void displayRate(SimpleRatingBar ratingBar,double rate) {
        ratingBar.setRating((float)rate);
    }

    @BindingAdapter("progressRate")
    public static void displayProgressRate(ProgressBar progressBar, double rate) {

        int r = (int) Math.round(100/rate);
        progressBar.setProgress(r);

    }


}
