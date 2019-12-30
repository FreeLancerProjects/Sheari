package com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments;

import android.content.Context;
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

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.ProviderSignUpActivity;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.databinding.FragmentProviderStep2Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;
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

public class Fragment_Provider_Step2 extends Fragment {

    private FragmentProviderStep2Binding binding;
    private ProviderSignUpActivity activity;
    private String lang;
    private Listeners.ProviderSteps listener = null;
    private ProviderSignUpModel providerSignUpModel;
    private List<LocationModel> cityList, countryList,regionList;
    private LocationSpinnerAdapter citySpinnerAdapter, countrySpinnerAdapter,regionSpinnerAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (listener==null)
        {
            listener = (Listeners.ProviderSteps) context;
        }
    }


    public static Fragment_Provider_Step2 newInstance() {
        return new Fragment_Provider_Step2();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_step2, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {

        regionList = new ArrayList<>();
        countryList = new ArrayList<>();
        cityList = new ArrayList<>();

        regionList.add(new LocationModel(0, getString(R.string.region)));
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));

        activity = (ProviderSignUpActivity) getActivity();
        providerSignUpModel = activity.getProviderSignUpModel();
        binding.setSignUpModel(providerSignUpModel);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());


        countrySpinnerAdapter = new LocationSpinnerAdapter(activity,countryList);
        binding.spinnerCountry.setAdapter(countrySpinnerAdapter);

        citySpinnerAdapter = new LocationSpinnerAdapter(activity,cityList);
        binding.spinnerCity.setAdapter(citySpinnerAdapter);

        regionSpinnerAdapter = new LocationSpinnerAdapter(activity,regionList);
        binding.spinnerRegion.setAdapter(regionSpinnerAdapter);



        binding.rb1.setOnClickListener(view -> {
            binding.rb2.setChecked(false);
            binding.rb3.setChecked(false);
            binding.rb4.setChecked(false);
            providerSignUpModel.setCompany_type(1);

        });

        binding.rb2.setOnClickListener(view -> {
            binding.rb1.setChecked(false);
            binding.rb3.setChecked(false);
            binding.rb4.setChecked(false);
            providerSignUpModel.setCompany_type(2);


        });

        binding.rb3.setOnClickListener(view -> {
            binding.rb1.setChecked(false);
            binding.rb2.setChecked(false);
            binding.rb4.setChecked(false);
            providerSignUpModel.setCompany_type(3);


        });

        binding.rb4.setOnClickListener(view -> {
            binding.rb1.setChecked(false);
            binding.rb2.setChecked(false);
            binding.rb3.setChecked(false);
            providerSignUpModel.setCompany_type(4);


        });

        binding.btnNext.setOnClickListener((view ->{
            if (providerSignUpModel.step2IsValid(activity))
            {
                listener.step3();
                activity.setProviderSignUpModel(providerSignUpModel);
            }
        }
        ));


        binding.btnPrevious.setOnClickListener((view -> activity.back()));

        try {
            binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        cityList.clear();
                        cityList.add(new LocationModel(0, getString(R.string.city2)));
                        citySpinnerAdapter.notifyDataSetChanged();
                        providerSignUpModel.setCountry_id(0);

                    }else
                    {
                        providerSignUpModel.setCountry_id(countryList.get(i).getId());

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
                        providerSignUpModel.setCity_id(0);
                        providerSignUpModel.setRegion_id(0);

                        regionList.clear();
                        regionList.add(new LocationModel(0, getString(R.string.region)));
                        regionSpinnerAdapter.notifyDataSetChanged();

                    }else
                    {
                        int city_id = cityList.get(i).getId();

                        providerSignUpModel.setCity_id(city_id);

                        getRegion(city_id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });




            binding.spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        providerSignUpModel.setRegion_id(0);

                    }else
                    {
                        int region_id = regionList.get(i).getId();
                        providerSignUpModel.setRegion_id(region_id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }catch (Exception e){}
        getCountry();
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
                                activity.runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());

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
                                activity.runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());



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

    private void getRegion(int city_id)
    {
        Api.getService(Tags.base_url)
                .getRegionByCity(lang,city_id)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                try {
                                    regionList.clear();
                                    regionList.add(new LocationModel(0, getString(R.string.region)));
                                    regionList.addAll(response.body().getData());
                                    activity.runOnUiThread(()->regionSpinnerAdapter.notifyDataSetChanged());

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
}
