package com.creative.share.apps.sheari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.CategorySpinnerRowBinding;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryModel>  list;

    public CategorySpinnerAdapter(Context context, List<CategoryModel> list) {
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
        @SuppressLint("ViewHolder") CategorySpinnerRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.category_spinner_row,viewGroup,false);
        binding.setModel(list.get(i));
        return binding.getRoot();
    }
}
