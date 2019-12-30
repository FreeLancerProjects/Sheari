package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step4;
import com.creative.share.apps.sheari.databinding.SubCatRowBinding;
import com.creative.share.apps.sheari.models.CategoryModel;

import java.util.List;

public class SubCategoryAdapter2 extends RecyclerView.Adapter<SubCategoryAdapter2.MyHolder> {

    private List<CategoryModel> list;
    private Context context;
    private List<Integer> selected_ids;
    private Fragment_Provider_Step4 fragment;
    public SubCategoryAdapter2(List<CategoryModel> list, Context context,List<Integer> selected_ids,Fragment_Provider_Step4 fragment) {

        this.list = list;
        this.context = context;
        this.selected_ids = selected_ids;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public SubCategoryAdapter2.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SubCatRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.sub_cat_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryAdapter2.MyHolder holder, int position) {


        CategoryModel model = list.get(position);
        holder.binding.setModel(model);

        int id = model.getId();
        if (hasItem(id))
        {
            holder.binding.checkbox.setChecked(true);
        }else
            {
                holder.binding.checkbox.setChecked(false);

            }


        holder.binding.checkbox.setOnClickListener(view ->
        {

            CategoryModel model2 = list.get(holder.getAdapterPosition());

            if (holder.binding.checkbox.isChecked())
            {

                fragment.addItem(model2);

            }else
                {
                    if (selected_ids.size()>0)
                    {
                        fragment.removeItem(model2.getId());
                    }



                }
        });


    }

    private boolean hasItem(int selected_id)
    {
        for (Integer id:selected_ids)
        {

            if (selected_id == id)
            {
                return true;
            }
        }

        return false;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private SubCatRowBinding binding;
        public MyHolder(SubCatRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

