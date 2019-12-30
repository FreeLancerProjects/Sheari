package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.MyFieldsRow2Binding;
import com.creative.share.apps.sheari.models.UserModel;

import java.util.List;

import io.paperdb.Paper;

public class MyFieldAdapter2 extends RecyclerView.Adapter<MyFieldAdapter2.MyHolder> {

    private List<UserModel.Data.SubCategory> list;
    private Context context;
    private String lang;
    public MyFieldAdapter2(List<UserModel.Data.SubCategory> list, Context context) {

        this.list = list;
        this.context = context;
        lang = Paper.book().read("lang","ar");

    }

    @NonNull
    @Override
    public MyFieldAdapter2.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MyFieldsRow2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.my_fields_row2,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyFieldAdapter2.MyHolder holder, int position) {


        UserModel.Data.SubCategory model = list.get(position);
        holder.binding.setLang(lang);
        holder.binding.setModel(model);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private MyFieldsRow2Binding binding;
        public MyHolder(MyFieldsRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

