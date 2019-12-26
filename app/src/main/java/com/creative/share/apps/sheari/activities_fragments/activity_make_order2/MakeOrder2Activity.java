package com.creative.share.apps.sheari.activities_fragments.activity_make_order2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.adapters.CategorySpinnerAdapter;
import com.creative.share.apps.sheari.databinding.ActivityMakeOrder2Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.ProviderModel;
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

public class MakeOrder2Activity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMakeOrder2Binding binding;
    private String lang;
    private int cat_id;
    private ProviderModel providerModel=null;
    private List<CategoryModel> spinnerCategoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_make_order2 );
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("cat_id")&&intent.hasExtra("data"))
        {
            cat_id = intent.getIntExtra("cat_id",0);
            providerModel = (ProviderModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        spinnerCategoryList = new ArrayList<>();
        spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));

        categorySpinnerAdapter = new CategorySpinnerAdapter(this,spinnerCategoryList);
        binding.spinnerCategory.setAdapter(categorySpinnerAdapter);
        getData();

    }


    private void getData() {
        Api.getService(Tags.base_url)
                .getCategory(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {


                                spinnerCategoryList.clear();
                                spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));
                                spinnerCategoryList.addAll(response.body().getData());
                                categorySpinnerAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(MakeOrder2Activity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(MakeOrder2Activity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MakeOrder2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MakeOrder2Activity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MakeOrder2Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
