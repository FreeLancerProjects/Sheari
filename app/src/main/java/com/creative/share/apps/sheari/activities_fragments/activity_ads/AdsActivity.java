package com.creative.share.apps.sheari.activities_fragments.activity_ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_providers.ProvidersActivity;
import com.creative.share.apps.sheari.adapters.AdsAdapter;
import com.creative.share.apps.sheari.databinding.ActivityAdsBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
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

public class AdsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityAdsBinding binding;
    private String lang;
    private List<CategoryModel> adsList;
    private AdsAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ads);
        initView();
    }


    private void initView() {
        adsList = new ArrayList<>();

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new AdsAdapter(adsList,this);
        binding.recView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        Api.getService(Tags.base_url)
                .getAds(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                adsList.clear();
                                adsList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();

                                if (adsList.size()>0)
                                {
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else
                                    {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }

                            } else {
                                Toast.makeText(AdsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(AdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(AdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void setItemData(CategoryModel model) {

        Intent intent = new Intent(this, ProvidersActivity.class);
        intent.putExtra("data",model);
        startActivity(intent);
    }
}
