package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.SliderRowBinding;
import com.creative.share.apps.sheari.models.SliderDataModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private List<SliderDataModel.SliderModel> sliderModelList;

    public SliderAdapter(Context context, List<SliderDataModel.SliderModel> sliderModelList) {
        this.context = context;
        this.sliderModelList = sliderModelList;
    }

    @Override
    public int getCount() {
        return sliderModelList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SliderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.slider_row,container,false);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        Picasso.with(context).load(Uri.parse(sliderModelList.get(position).getName())).fit().into(binding.image, new Callback() {
            @Override
            public void onSuccess() {
                binding.progBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
