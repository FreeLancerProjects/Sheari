package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_offers.OffersActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_order.OrderActivity;
import com.creative.share.apps.sheari.databinding.LoadMoreRowBinding;
import com.creative.share.apps.sheari.databinding.OfferRowBinding;
import com.creative.share.apps.sheari.models.OfferDataModel;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;

    private List<OfferDataModel.Data.Provider> list;
    private Context context;
    private AppCompatActivity activity;

    public OfferAdapter(List<OfferDataModel.Data.Provider> list, Context context) {

        this.list = list;
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            OfferRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.offer_row,parent,false);

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
            OfferDataModel.Data.Provider providerModel = list.get(position);
            myHolder.binding.setModel(providerModel);

            myHolder.itemView.setOnClickListener(view ->
            {
                OfferDataModel.Data.Provider providerModel2 = list.get(myHolder.getAdapterPosition());
                if (activity instanceof OffersActivity)
                {
                    OffersActivity offersActivity = (OffersActivity) activity;
                    offersActivity.setItemData(providerModel2);
                }else if (activity instanceof OrderActivity)
                {
                    OrderActivity orderActivity = (OrderActivity) activity;
                    orderActivity.setItemData(providerModel2);
                }
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
        private OfferRowBinding binding;
        public MyHolder(OfferRowBinding binding) {
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
       OfferDataModel.Data.Provider providerModel = list.get(position);
        if (providerModel == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;

        }



    }
}
