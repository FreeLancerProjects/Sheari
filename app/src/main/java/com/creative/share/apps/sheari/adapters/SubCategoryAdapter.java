package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_sub_category.SubCategoryActivity;
import com.creative.share.apps.sheari.databinding.SubCategoryRowBinding;
import com.creative.share.apps.sheari.models.CategoryModel;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyHolder> {

    private List<CategoryModel> list;
    private Context context;
    private SubCategoryActivity activity;

    public SubCategoryAdapter(List<CategoryModel> list, Context context) {

        this.list = list;
        this.context = context;
        this.activity = (SubCategoryActivity) context;

    }

    @NonNull
    @Override
    public SubCategoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SubCategoryRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.sub_category_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryAdapter.MyHolder holder, int position) {


        CategoryModel model = list.get(position);
        holder.binding.setModel(model);

        holder.itemView.setOnClickListener(view ->
        {
            CategoryModel model2 = list.get(holder.getAdapterPosition());
            activity.setItemData(model2);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private SubCategoryRowBinding binding;
        public MyHolder(SubCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

