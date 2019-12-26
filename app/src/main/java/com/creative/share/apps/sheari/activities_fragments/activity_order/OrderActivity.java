package com.creative.share.apps.sheari.activities_fragments.activity_order;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_order_offer_details.OrderOfferDetailsActivity;
import com.creative.share.apps.sheari.adapters.OfferAdapter;
import com.creative.share.apps.sheari.databinding.ActivityOrderBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.OfferDataModel;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityOrderBinding binding;
    private String lang;
    private List<OfferDataModel.Data.Provider> providerList;
    private int current_page=1;
    private boolean isLoading = false;
    private GridLayoutManager manager;
    private OfferAdapter adapter;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order);
        initView();
    }


    private void initView() {
        providerList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        adapter = new OfferAdapter(providerList,this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new GridLayoutManager(this,2);
        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int total_item = adapter.getItemCount();
                    int last_item = manager.findLastCompletelyVisibleItemPosition();

                    if (total_item>6&& (last_item==(total_item-1)||last_item==(total_item-2))&&!isLoading)
                    {
                        int page = current_page+1;
                        isLoading = true;
                        providerList.add(null);
                        adapter.notifyItemInserted(providerList.size()-1);
                        loadMore(page);
                    }

                }
            }
        });

        getData();
    }

    private void getData()
    {
        Api.getService(Tags.base_url)
                .getOrder(0)
                .enqueue(new Callback<OfferDataModel>() {
                    @Override
                    public void onResponse(Call<OfferDataModel> call, Response<OfferDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                providerList.clear();
                                providerList.addAll(response.body().getData().getProviders());
                                adapter.notifyDataSetChanged();
                                if (providerList.size()>0)
                                {
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else
                                {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }

                            } else {
                                Toast.makeText(OrderActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(OrderActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OfferDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void loadMore(int page)
    {
        Api.getService(Tags.base_url)
                .getOrder(page)
                .enqueue(new Callback<OfferDataModel>() {
                    @Override
                    public void onResponse(Call<OfferDataModel> call, Response<OfferDataModel> response) {
                        providerList.remove(providerList.size()-1);
                        adapter.notifyItemRemoved(providerList.size()-1);
                        isLoading = false;

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                providerList.addAll(response.body().getData().getProviders());
                                adapter.notifyDataSetChanged();



                                if (providerList.size()>0)
                                {
                                    current_page = response.body().getData().getPaginate().getCurrent_page();
                                }

                            } else {
                                Toast.makeText(OrderActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(OrderActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OfferDataModel> call, Throwable t) {
                        try {
                            if (providerList.get(providerList.size()-1)==null)
                            {
                                providerList.remove(providerList.size()-1);
                                adapter.notifyItemRemoved(providerList.size()-1);
                                isLoading = false;
                            }



                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }
    @Override
    public void back() {
        finish();
    }


    public void setItemData(OfferDataModel.Data.Provider providerModel) {

        Intent intent = new Intent(this, OrderOfferDetailsActivity.class);
        intent.putExtra("data",providerModel);
        startActivity(intent);
    }
}
