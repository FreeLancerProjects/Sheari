package com.creative.share.apps.sheari.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_sub_category.SubCategoryActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.adapters.CategorySpinnerAdapter;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.adapters.MainCategoryAdapter;
import com.creative.share.apps.sheari.databinding.FragmentHomeBinding;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.preferences.Preferences;
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

public class Fragment_Home extends Fragment {
    private FragmentHomeBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private List<CategoryModel> categoryModelList;
    private MainCategoryAdapter adapter;
    private List<LocationModel> cityList, countryList;
    private LocationSpinnerAdapter citySpinnerAdapter, countrySpinnerAdapter;
    private List<CategoryModel> spinnerCategoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter;


    public static Fragment_Home newInstance() {
        return new Fragment_Home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        countryList = new ArrayList<>();
        cityList = new ArrayList<>();
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));

        spinnerCategoryList = new ArrayList<>();
        spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));

        categoryModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new MainCategoryAdapter(categoryModelList, activity,this);
        binding.recView.setAdapter(adapter);

        //////////////////////////////////////////////////////////////////////////////
        countrySpinnerAdapter = new LocationSpinnerAdapter(activity,countryList);
        binding.spinnerCountry.setAdapter(countrySpinnerAdapter);

        citySpinnerAdapter = new LocationSpinnerAdapter(activity,cityList);
        binding.spinnerCity.setAdapter(citySpinnerAdapter);

        categorySpinnerAdapter = new CategorySpinnerAdapter(activity,spinnerCategoryList);
        binding.spinnerCategory.setAdapter(categorySpinnerAdapter);
        try {
            binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        cityList.clear();
                        cityList.add(new LocationModel(0, getString(R.string.city2)));
                        citySpinnerAdapter.notifyDataSetChanged();
                    }else
                    {
                        getCity(countryList.get(i).getId());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }catch (Exception e){}

        //////////////////////////////////////////////////////////////////////////////
        getData();
        getCountry();


    }


    private void getData() {
        Api.getService(Tags.base_url)
                .getCategory(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                categoryModelList.clear();
                                categoryModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();

                                spinnerCategoryList.clear();
                                spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));
                                spinnerCategoryList.addAll(response.body().getData());
                                categorySpinnerAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getCountry() {
        Api.getService(Tags.base_url)
                .getCountry(lang)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                countryList.clear();
                                countryList.add(new LocationModel(0, getString(R.string.country2)));
                                countryList.addAll(response.body().getData());
                                citySpinnerAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDataModel> call, Throwable t) {
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
    }

    private void getCity(int country_id) {
        Api.getService(Tags.base_url)
                .getCityByCountry(lang,country_id)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                cityList.clear();
                                cityList.add(new LocationModel(0, getString(R.string.city2)));
                                cityList.addAll(response.body().getData());
                                citySpinnerAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDataModel> call, Throwable t) {
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
    }


    public void setItemData(CategoryModel model) {

        Intent intent = new Intent(activity, SubCategoryActivity.class);
        intent.putExtra("data",model);
        startActivity(intent);
    }


}
