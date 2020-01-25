package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.LoadMoreRowBinding;
import com.creative.share.apps.sheari.databinding.NotificationRowBinding;
import com.creative.share.apps.sheari.models.NotificationDataModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;

    private List<NotificationDataModel.Data.NotificationModel> list;
    private Context context;

    public NotificationAdapter(List<NotificationDataModel.Data.NotificationModel> list, Context context) {

        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            NotificationRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.notification_row,parent,false);

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
            NotificationDataModel.Data.NotificationModel model = list.get(myHolder.getAdapterPosition());
            myHolder.binding.setModel(model);



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
        private NotificationRowBinding binding;
        public MyHolder(NotificationRowBinding binding) {
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
        NotificationDataModel.Data.NotificationModel model = list.get(position);
        if (model == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;

        }



    }
}
