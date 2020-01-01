package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_update_provider_profile.UpdateProviderProfileActivity;
import com.creative.share.apps.sheari.databinding.ProjectRowBinding;
import com.creative.share.apps.sheari.models.ProjectModel;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyHolder> {

    private List<ProjectModel> list;
    private Context context;
    private boolean visible;
    private AppCompatActivity activity;
    public ProjectAdapter(List<ProjectModel> list, Context context,boolean visible) {
        this.visible = visible;
        this.list = list;
        this.context = context;
        activity = (AppCompatActivity) context;

    }

    @NonNull
    @Override
    public ProjectAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ProjectRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.project_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ProjectAdapter.MyHolder holder, int position) {


        ProjectModel model = list.get(position);
        holder.binding.setModel(model);
        holder.binding.setVisible(visible);

        holder.binding.btnUpdate.setOnClickListener(view -> {
            ProjectModel model2 = list.get(holder.getAdapterPosition());

            UpdateProviderProfileActivity updateProviderProfileActivity = (UpdateProviderProfileActivity) activity;
            updateProviderProfileActivity.setItem(model2,holder.getAdapterPosition());

        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ProjectRowBinding binding;
        public MyHolder(ProjectRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

