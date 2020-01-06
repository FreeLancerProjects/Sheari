package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.CommentRowBinding;
import com.creative.share.apps.sheari.models.CommentDataModel;
import com.creative.share.apps.sheari.models.UserModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyHolder> {

    private List<CommentDataModel.Data.Comment> list;
    private Context context;
    UserModel.Data user ;
    public CommentAdapter(List<CommentDataModel.Data.Comment> list, Context context, UserModel.Data user) {
        this.list = list;
        this.context = context;
        this.user = user;
    }

    @NonNull
    @Override
    public CommentAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CommentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.comment_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.MyHolder holder, int position) {


        CommentDataModel.Data.Comment model = list.get(position);
        holder.binding.setModel(model);
        holder.binding.setModel(model);
        holder.binding.setUser(user);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CommentRowBinding binding;
        public MyHolder(CommentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

