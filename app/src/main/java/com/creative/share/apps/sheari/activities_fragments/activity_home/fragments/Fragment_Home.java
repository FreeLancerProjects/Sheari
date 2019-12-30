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
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_providers.ProvidersActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_sub_category.SubCategoryActivity;
import com.creative.share.apps.sheari.adapters.CategorySpinnerAdapter;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.adapters.MainCategoryAdapter;
import com.creative.share.apps.sheari.adapters.SliderAdapter;
import com.creative.share.apps.sheari.databinding.FragmentHomeBinding;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.models.SliderDataModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    private int sub_cat_id =0,country_id=0,city_id=0;
    private SliderAdapter sliderAdapter;
    private List<SliderDataModel.SliderModel> sliderModelList;
    private TimerTask timerTask;
    private Timer timer;


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
        sliderModelList = new ArrayList<>();
        countryList = new ArrayList<>();
        cityList = new ArrayList<>();
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));

        spinnerCategoryList = new ArrayList<>();
        spinnerCategoryList.add(new CategoryModel(getString(R.string.sub_cat)));

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
                        city_id=0;
                        cityList.clear();
                        cityList.add(new LocationModel(0, getString(R.string.city2)));
                        citySpinnerAdapter.notifyDataSetChanged();
                    }else
                    {
                        country_id = countryList.get(i).getId();
                        getCity(countryList.get(i).getId());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        city_id = 0;


                    }else
                    {
                        city_id = cityList.get(i).getId();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        sub_cat_id = 0;


                    }else
                    {
                        sub_cat_id = spinnerCategoryList.get(i).getId();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }catch (Exception e){}

        //////////////////////////////////////////////////////////////////////////////

        binding.flSearch.setOnClickListener(view ->
        {
            if (country_id!=0&&city_id!=0&& sub_cat_id !=0)
            {
                Intent intent = new Intent(activity, ProvidersActivity.class);
                intent.putExtra("sub_cat_id",sub_cat_id);
                intent.putExtra("country_id",country_id);
                intent.putExtra("city_id",city_id);
                startActivity(intent);
            }else
            {
                if (country_id==0)
                {
                    Toast.makeText(activity, getString(R.string.ch_country), Toast.LENGTH_SHORT).show();
                }

                if (city_id==0)
                {
                    Toast.makeText(activity, getString(R.string.ch_city), Toast.LENGTH_SHORT).show();
                }

                if (sub_cat_id ==0)
                {
                    Toast.makeText(activity, getString(R.string.ch_dept), Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSliderData();
        getData();
        getSubCategory();
        getCountry();


    }

    private void getSliderData() {
        Api.getService(Tags.base_url)
                .getSlider()
                .enqueue(new Callback<SliderDataModel>() {
                    @Override
                    public void onResponse(Call<SliderDataModel> call, Response<SliderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                updateSliderUI(response.body().getData());

                               /* spinnerCategoryList.clear();
                                spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));
                                spinnerCategoryList.addAll(response.body().getData());
                                categorySpinnerAdapter.notifyDataSetChanged();*/

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
                    public void onFailure(Call<SliderDataModel> call, Throwable t) {
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

    private void updateSliderUI(List<SliderDataModel.SliderModel> data) {
        if (data.size()>0)
        {
            binding.tvNoAds.setVisibility(View.GONE);
            sliderModelList.clear();
            sliderModelList.addAll(data);
            sliderAdapter  = new SliderAdapter(activity,sliderModelList);
            binding.pager.setAdapter(sliderAdapter);
            binding.tab.setupWithViewPager(binding.pager);
            if (data.size()>1)
            {
                timer = new Timer();
                timerTask = new MyTimerTask();
                timer.scheduleAtFixedRate(timerTask,6000,6000);

            }

        }else
            {
                binding.tvNoAds.setVisibility(View.VISIBLE);
            }
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
                               try {
                                   categoryModelList.clear();
                                   categoryModelList.addAll(response.body().getData());
                                   adapter.notifyDataSetChanged();
                               }catch (Exception e){}


                               /* spinnerCategoryList.clear();
                                spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));
                                spinnerCategoryList.addAll(response.body().getData());
                                categorySpinnerAdapter.notifyDataSetChanged();*/

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

    private void getSubCategory() {
        Api.getService(Tags.base_url)
                .getAllSubCategory(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                try {
                                    spinnerCategoryList.clear();
                                    spinnerCategoryList.add(new CategoryModel(getString(R.string.sub_cat)));
                                    spinnerCategoryList.addAll(response.body().getData());
                                    activity.runOnUiThread(()->categorySpinnerAdapter.notifyDataSetChanged());


                                }catch (Exception e){}

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

                               try {
                                   countryList.clear();
                                   countryList.add(new LocationModel(0, getString(R.string.country2)));
                                   countryList.addAll(response.body().getData());

                                   activity.runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());

                               }catch (Exception e){}

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

                                try {
                                    cityList.clear();
                                    cityList.add(new LocationModel(0, getString(R.string.city2)));
                                    cityList.addAll(response.body().getData());
                                    activity.runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());

                                }catch (Exception e){}


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

    private class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            activity.runOnUiThread(() -> {

                if (binding.pager.getCurrentItem()<binding.pager.getAdapter().getCount()-1)
                {
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem()+1);
                }else
                    {
                        binding.pager.setCurrentItem(0);

                    }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (timer!=null&&timerTask!=null)
        {
            timerTask.cancel();
            timer.purge();
            timer.cancel();
        }
        super.onDestroy();
    }
}
