package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_providers.ProvidersActivity;
import com.creative.share.apps.sheari.databinding.LoadMoreRowBinding;
import com.creative.share.apps.sheari.databinding.ProviderRowBinding;
import com.creative.share.apps.sheari.models.ProvidersDataModel;

import java.util.List;

public class ProvidersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;

    private List<ProvidersDataModel.ProviderModel> list;
    private Context context;
    private ProvidersActivity activity;

    public ProvidersAdapter(List<ProvidersDataModel.ProviderModel> list, Context context) {

        this.list = list;
        this.context = context;
        this.activity = (ProvidersActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            ProviderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.provider_row,parent,false);

            return new MyHolder(binding);
        } else {
            LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more_row,parent,false);

            return new LoadMoreHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {

            final MyHolder myHolder = (MyHolder) holder;
            ProvidersDataModel.ProviderModel providerModel = list.get(myHolder.getAdapterPosition());
            myHolder.binding.setModel(providerModel);

            myHolder.itemView.setOnClickListener(view ->
            {
                activity.setItemData(providerModel);
            });

        } else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.binding.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ProviderRowBinding binding;
        public MyHolder(ProviderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;




        }

    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private LoadMoreRowBinding binding;
        public LoadMoreHolder(LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ProvidersDataModel.ProviderModel providerModel = list.get(position);
        if (providerModel == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;

        }



    }
}
