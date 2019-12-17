package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.home_activity.fragments.Fragment_Home;
import com.creative.share.apps.sheari.databinding.MainCategoryRowBinding;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;

import java.util.List;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.MyHolder> {

    private List<CategoryModel> list;
    private Context context;
    private int colors [] = {R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5,R.color.color6,R.color.color7,R.color.color8,R.color.color9,R.color.color10};
    private Fragment_Home fragment;

    public MainCategoryAdapter(List<CategoryModel> list, Context context,Fragment_Home fragment) {

        this.list = list;
        this.context = context;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public MainCategoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MainCategoryRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.main_category_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final MainCategoryAdapter.MyHolder holder, int position) {

        int pos_color = holder.getAdapterPosition()%colors.length;

        CategoryModel model = list.get(position);
        holder.binding.setModel(model);
        holder.binding.setPos(holder.getAdapterPosition()+1);
        holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context,colors[pos_color]));
        holder.binding.tvPos.setBackgroundResource(colors[pos_color]);

        holder.itemView.setOnClickListener(view ->
        {
            CategoryModel model2 = list.get(holder.getAdapterPosition());
            fragment.setItemData(model2);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private MainCategoryRowBinding binding;
        public MyHolder(MainCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

