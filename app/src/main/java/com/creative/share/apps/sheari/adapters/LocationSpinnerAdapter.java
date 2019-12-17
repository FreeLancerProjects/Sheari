package com.creative.share.apps.sheari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.LocationSpinnerRowBinding;
import com.creative.share.apps.sheari.models.LocationModel;

import java.util.List;

public class LocationSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<LocationModel>  list;

    public LocationSpinnerAdapter(Context context, List<LocationModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") LocationSpinnerRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.location_spinner_row,viewGroup,false);
        binding.setModel(list.get(i));
        return binding.getRoot();
    }
}
