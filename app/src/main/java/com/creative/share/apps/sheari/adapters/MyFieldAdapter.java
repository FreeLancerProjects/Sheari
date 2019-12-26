package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.MyFieldsRowBinding;
import com.creative.share.apps.sheari.models.ProviderModel;

import java.util.List;

import io.paperdb.Paper;

public class MyFieldAdapter extends RecyclerView.Adapter<MyFieldAdapter.MyHolder> {

    private List<ProviderModel.ProviderSubCategory> list;
    private Context context;
    private String lang;
    public MyFieldAdapter(List<ProviderModel.ProviderSubCategory> list, Context context) {

        this.list = list;
        this.context = context;
        lang = Paper.book().read("lang","ar");

    }

    @NonNull
    @Override
    public MyFieldAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MyFieldsRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.my_fields_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyFieldAdapter.MyHolder holder, int position) {


        ProviderModel.ProviderSubCategory model = list.get(position);
        holder.binding.setLang(lang);
        holder.binding.setModel(model);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private MyFieldsRowBinding binding;
        public MyHolder(MyFieldsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

