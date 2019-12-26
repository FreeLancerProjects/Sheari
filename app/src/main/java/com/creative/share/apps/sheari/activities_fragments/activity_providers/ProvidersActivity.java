package com.creative.share.apps.sheari.activities_fragments.activity_providers;

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
import com.creative.share.apps.sheari.activities_fragments.activity_make_order.CreateOrderActivity;
import com.creative.share.apps.sheari.adapters.ProvidersAdapter;
import com.creative.share.apps.sheari.databinding.ActivityProvidersBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.ProviderModel;
import com.creative.share.apps.sheari.models.ProvidersDataModel;
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

public class ProvidersActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProvidersBinding binding;
    private String lang;
    private int cat_id;
    private CategoryModel subCategoryModel = null;
    private List<ProviderModel> providerModelList;
    private ProvidersAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private GridLayoutManager manager;
    private int type;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_providers);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("cat_id") && intent.hasExtra("data")) {
            cat_id = intent.getIntExtra("cat_id", 0);
            type = 1;

            subCategoryModel = (CategoryModel) intent.getSerializableExtra("data");
        } else {
            type = 2;
            subCategoryModel = (CategoryModel) intent.getSerializableExtra("data");

        }
    }

    private void initView() {
        providerModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        manager = new GridLayoutManager(this, 2);
        binding.recView.setLayoutManager(manager);
        adapter = new ProvidersAdapter(providerModelList, this);
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int completePos = manager.findLastCompletelyVisibleItemPosition();
                    if (completePos < (providerModelList.size() - 1) && !isLoading) {
                        int page = current_page + 1;
                        isLoading = true;
                        if (type == 1) {
                            loadMore(page);

                        } else if (type == 2) {
                            loadAdsMore(page);
                        }
                    }
                }
            }
        });
        if (type == 1) {
            getProviders();

        } else if (type == 2) {
            getAdsProviders();
        }
    }

    private void getProviders() {
        Api.getService(Tags.base_url)
                .getProvidersBySubCategory(lang, subCategoryModel.getId(), 1)
                .enqueue(new Callback<ProvidersDataModel>() {
                    @Override
                    public void onResponse(Call<ProvidersDataModel> call, Response<ProvidersDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                providerModelList.clear();
                                providerModelList.addAll(response.body().getData().getProviders());
                                adapter.notifyDataSetChanged();
                                if (providerModelList.size() > 0) {
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                Toast.makeText(ProvidersActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProvidersActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ProvidersActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProvidersDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProvidersActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProvidersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }


    private void loadMore(int page) {
        Api.getService(Tags.base_url)
                .getProvidersBySubCategory(lang, subCategoryModel.getId(), page)
                .enqueue(new Callback<ProvidersDataModel>() {
                    @Override
                    public void onResponse(Call<ProvidersDataModel> call, Response<ProvidersDataModel> response) {
                        providerModelList.remove(providerModelList.size() - 1);
                        adapter.notifyItemRemoved(providerModelList.size() - 1);
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                if (response.body().getData().getProviders().size() > 0) {
                                    providerModelList.addAll(response.body().getData().getProviders());
                                    adapter.notifyDataSetChanged();
                                    current_page = response.body().getData().getPaginate().getCurrent_page();

                                }

                            } else {
                                Toast.makeText(ProvidersActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProvidersActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ProvidersActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProvidersDataModel> call, Throwable t) {
                        try {

                            isLoading = false;

                            if (providerModelList.get(providerModelList.size() - 1) == null) {
                                providerModelList.remove(providerModelList.size() - 1);
                                adapter.notifyItemRemoved(providerModelList.size() - 1);

                            }
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProvidersActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProvidersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getAdsProviders() {
        Api.getService(Tags.base_url)
                .getAdsProvidersBySubCategory(subCategoryModel.getId(), 1)
                .enqueue(new Callback<ProvidersDataModel>() {
                    @Override
                    public void onResponse(Call<ProvidersDataModel> call, Response<ProvidersDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                providerModelList.clear();
                                providerModelList.addAll(response.body().getData().getProviders());
                                adapter.notifyDataSetChanged();
                                if (providerModelList.size() > 0) {
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                Toast.makeText(ProvidersActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProvidersActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ProvidersActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProvidersDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProvidersActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProvidersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void loadAdsMore(int page) {
        Api.getService(Tags.base_url)
                .getAdsProvidersBySubCategory(subCategoryModel.getId(), page)
                .enqueue(new Callback<ProvidersDataModel>() {
                    @Override
                    public void onResponse(Call<ProvidersDataModel> call, Response<ProvidersDataModel> response) {
                        providerModelList.remove(providerModelList.size() - 1);
                        adapter.notifyItemRemoved(providerModelList.size() - 1);
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                if (response.body().getData().getProviders().size() > 0) {
                                    providerModelList.addAll(response.body().getData().getProviders());
                                    adapter.notifyDataSetChanged();
                                    current_page = response.body().getData().getPaginate().getCurrent_page();

                                }

                            } else {
                                Toast.makeText(ProvidersActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProvidersActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ProvidersActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProvidersDataModel> call, Throwable t) {
                        try {

                            isLoading = false;

                            if (providerModelList.get(providerModelList.size() - 1) == null) {
                                providerModelList.remove(providerModelList.size() - 1);
                                adapter.notifyItemRemoved(providerModelList.size() - 1);

                            }
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProvidersActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProvidersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void setItemData(ProviderModel providerModel) {
        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra("cat_id", cat_id);
        intent.putExtra("data", providerModel);
        startActivity(intent);

    }
}
