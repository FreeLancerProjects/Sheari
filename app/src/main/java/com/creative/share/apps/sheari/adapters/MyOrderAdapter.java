package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Current;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Pending;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Previous;
import com.creative.share.apps.sheari.databinding.LoadMoreRowBinding;
import com.creative.share.apps.sheari.databinding.OrderRowBinding;
import com.creative.share.apps.sheari.models.MyOrderDataModel;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;

    private List<MyOrderDataModel.Data.OrderModel> list;
    private Context context;
    private Fragment fragment;

    public MyOrderAdapter(List<MyOrderDataModel.Data.OrderModel> list, Context context,Fragment fragment) {

        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            OrderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.order_row,parent,false);

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
            MyOrderDataModel.Data.OrderModel model = list.get(myHolder.getAdapterPosition());
            myHolder.binding.setModel(model);

            myHolder.itemView.setOnClickListener(view ->
            {
                MyOrderDataModel.Data.OrderModel model2 = list.get(myHolder.getAdapterPosition());

                if (fragment instanceof Fragment_Order_Pending)
                {
                    Fragment_Order_Pending fragment_order_pending = (Fragment_Order_Pending) fragment;
                    fragment_order_pending.setItemData(model2,myHolder.getAdapterPosition());
                }else if (fragment instanceof Fragment_Order_Current)
                {

                    Fragment_Order_Current fragment_order_current = (Fragment_Order_Current) fragment;
                    fragment_order_current.setItemData(model2,myHolder.getAdapterPosition());
                }

                else if (fragment instanceof Fragment_Order_Previous)
                {

                    Fragment_Order_Previous fragment_order_previous = (Fragment_Order_Previous) fragment;
                    fragment_order_previous.setItemData(model2,myHolder.getAdapterPosition());
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
        private OrderRowBinding binding;
        public MyHolder(OrderRowBinding binding) {
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
        MyOrderDataModel.Data.OrderModel model = list.get(position);
        if (model == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;

        }



    }
}
