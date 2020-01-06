package com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.MyOrderActivity;
import com.creative.share.apps.sheari.adapters.MyOrderAdapter;
import com.creative.share.apps.sheari.databinding.FragmentOrderPendingCurrentPreviousBinding;
import com.creative.share.apps.sheari.models.MyOrderDataModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Order_Pending extends Fragment {
    public static final String TAG = "DATA";
    private FragmentOrderPendingCurrentPreviousBinding binding;
    private MyOrderActivity activity;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private int current_page = 1;
    private boolean isLoading = false;
    private LinearLayoutManager manager;
    private List<MyOrderDataModel.Data.OrderModel> orderModelList;
    private MyOrderAdapter adapter;


    public static Fragment_Order_Pending newInstance() {
        Fragment_Order_Pending fragment_order_pending = new Fragment_Order_Pending();
        return fragment_order_pending;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_pending_current_previous,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        orderModelList = new ArrayList<>();

        activity = (MyOrderActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);

        Paper.init(activity);
        lang = Paper.book().read("lang","ar");


        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        adapter = new MyOrderAdapter(orderModelList,activity,this);
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = manager.findLastCompletelyVisibleItemPosition();

                    if (total_item>=20&&(total_item-last_visible_item)==5&&!isLoading)
                    {

                        isLoading = true;
                        int page = current_page+1;
                        loadMore(page);
                    }
                }
            }
        });

        getOrders();

    }






    private void getOrders() {

        try {

            Call<MyOrderDataModel> call;

            if (userModel.getData().getRole().equals("client"))
            {
                call= Api.getService(Tags.base_url).getClientPendingOrder(lang,"Bearer "+userModel.getData().getToken(),1);
            }else
            {
                call= Api.getService(Tags.base_url).getProviderPendingOrder(lang,"Bearer "+userModel.getData().getToken(),1);


            }

            try {

                call.enqueue(new Callback<MyOrderDataModel>() {
                    @Override
                    public void onResponse(Call<MyOrderDataModel> call, Response<MyOrderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null ) {

                            if (response.body().isValue())
                            {
                                orderModelList.clear();
                                orderModelList.addAll(response.body().getData().getOrderModelList());
                                if (orderModelList.size() > 0) {

                                    adapter.notifyDataSetChanged();

                                    binding.tvNoOrder.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoOrder.setVisibility(View.VISIBLE);

                                }
                            }else
                            {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyOrderDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
            }catch (Exception e)
            {
                Log.e("error ",e.getMessage()+"__");
            }

        } catch (Exception e) {

        }
    }

    private void loadMore(int page) {
        try {


            Call<MyOrderDataModel> call;

            if (userModel.getData().getRole().equals("client"))
            {
                call= Api.getService(Tags.base_url).getClientPendingOrder(lang,"Bearer "+userModel.getData().getToken(),page);
            }else
            {
                call= Api.getService(Tags.base_url).getProviderPendingOrder(lang,"Bearer "+userModel.getData().getToken(),page);


            }

            try {
                call.enqueue(new Callback<MyOrderDataModel>() {
                    @Override
                    public void onResponse(Call<MyOrderDataModel> call, Response<MyOrderDataModel> response) {


                        isLoading = false;
                        orderModelList.remove(orderModelList.size() - 1);
                        adapter.notifyItemRemoved(orderModelList.size() - 1);

                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {


                            if (response.body().isValue())
                            {
                                int oldPos = orderModelList.size()-1;

                                orderModelList.addAll(response.body().getData().getOrderModelList());

                                if (response.body().getData().getOrderModelList().size() > 0) {
                                    current_page = response.body().getData().getPaginate().getCurrent_page();
                                    adapter.notifyItemRangeInserted(oldPos,orderModelList.size()-1);

                                }
                            }else
                            {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyOrderDataModel> call, Throwable t) {
                        try {
                            if (orderModelList.get(orderModelList.size() - 1) == null) {
                                isLoading = false;
                                orderModelList.remove(orderModelList.size() - 1);
                                adapter.notifyItemRemoved(orderModelList.size() - 1);

                            }
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

            }catch (Exception e)
            {
                Log.e("error ",e.getMessage()+"_");
            }
        } catch (Exception e) {

        }
    }


}
