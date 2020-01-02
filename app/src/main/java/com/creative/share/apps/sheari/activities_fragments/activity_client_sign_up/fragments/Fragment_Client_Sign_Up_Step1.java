package com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.ClientSignUpActivity;
import com.creative.share.apps.sheari.adapters.CodeCountryAdapter;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.databinding.DialogCountryCodeBinding;
import com.creative.share.apps.sheari.databinding.FragmentClientSignUpStep1Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.ClientSignUpModel;
import com.creative.share.apps.sheari.models.CountryCodeModel;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Sign_Up_Step1 extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener {
    private FragmentClientSignUpStep1Binding binding;
    private ClientSignUpActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private CountryPicker countryPicker;
    private ClientSignUpModel clientSignUpModel;
    private List<LocationModel> cityList, countryList,regionList;
    private LocationSpinnerAdapter citySpinnerAdapter, countrySpinnerAdapter,regionSpinnerAdapter;
    private Listener listener = null;
    private int region_id=0;
    private CodeCountryAdapter codeCountryAdapter;
    private List<CountryCodeModel> countryCodeModelList;
    private AlertDialog dialog;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (listener == null) {
            listener = (Listener) context;

        }
    }

    public static Fragment_Client_Sign_Up_Step1 newInstance() {

        Fragment_Client_Sign_Up_Step1 fragment_client_sign_up_step1 = new Fragment_Client_Sign_Up_Step1();

        return fragment_client_sign_up_step1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_sign_up_step1, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        countryCodeModelList = new ArrayList<>();
        activity = (ClientSignUpActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);

        countryList = new ArrayList<>();
        cityList = new ArrayList<>();
        regionList = new ArrayList<>();

        regionList.add(new LocationModel(0, getString(R.string.region)));
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));

        clientSignUpModel = new ClientSignUpModel();

        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setShowCountryDialogListener(this);


        binding.tvCode.setText("+966");
        clientSignUpModel.setPhone_code("966");

        binding.setClientSignUp(clientSignUpModel);
        //createCountryDialog();

        countryCodeModelList.add(new CountryCodeModel("المملكة العربية السعودية","Saudi Arabia","966",R.drawable.flag_sa));
        countryCodeModelList.add(new CountryCodeModel("الكويت","Kuwait","965",R.drawable.flag_kw));
        countryCodeModelList.add(new CountryCodeModel("البحرين","Bahrain","973",R.drawable.flag_bh));
        countryCodeModelList.add(new CountryCodeModel("عمان","Oman","968",R.drawable.flag_om));
        countryCodeModelList.add(new CountryCodeModel("الإمارات","United Arab Emirates","971",R.drawable.flag_ae));
        createCountryCodeDialog();







        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()&&editable.toString().startsWith("0"))
                {
                    binding.edtPhone.setText("");
                }
            }
        });
        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.btnNext.setOnClickListener((view ->
        {

            if (clientSignUpModel.step1IsValid(activity)) {
                listener.onStep1Valid(clientSignUpModel);
            }

        }
        ));



        countrySpinnerAdapter = new LocationSpinnerAdapter(activity,countryList);
        binding.spinnerCountry.setAdapter(countrySpinnerAdapter);

        citySpinnerAdapter = new LocationSpinnerAdapter(activity,cityList);
        binding.spinnerCity.setAdapter(citySpinnerAdapter);

        regionSpinnerAdapter = new LocationSpinnerAdapter(activity,regionList);
        binding.spinnerRegion.setAdapter(regionSpinnerAdapter);

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

            binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        clientSignUpModel.setRegion_id(0);
                        region_id=0;
                        regionList.clear();
                        regionList.add(new LocationModel(0, getString(R.string.region)));
                        activity.runOnUiThread(() -> regionSpinnerAdapter.notifyDataSetChanged());



                    }else
                    {
                        int city_id = cityList.get(i).getId();
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
                        region_id=0;
                        clientSignUpModel.setRegion_id(0);

                    }else
                    {
                        region_id = regionList.get(i).getId();
                        clientSignUpModel.setRegion_id(region_id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }catch (Exception e){}

        getCountry();

    }



    private void getCountry()
    {
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
    private void getCity(int country_id)
    {
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

    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM() != null) {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            } else {
                String code = "+966";
                binding.tvCode.setText(code);
                clientSignUpModel.setPhone_code(code.replace("+", "00"));
            }
        } catch (Exception e) {
            String code = "+966";
            binding.tvCode.setText(code);
            clientSignUpModel.setPhone_code(code.replace("+", "00"));
        }


    }

    private void updatePhoneCode(Country country) {

        binding.tvCode.setText(country.getDialCode());
        clientSignUpModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    @Override
    public void showDialog() {
        //countryPicker.showDialog(activity);
        dialog.show();
    }

    private void createCountryCodeDialog() {
        dialog = new AlertDialog.Builder(activity)
                .create();

        DialogCountryCodeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_country_code, null, false);

        codeCountryAdapter = new CodeCountryAdapter(countryCodeModelList,activity,this);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recView.setAdapter(codeCountryAdapter);


        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
    }





    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    public void setItemData(CountryCodeModel model) {

        binding.tvCode.setText("+"+model.getCode());
        clientSignUpModel.setPhone_code(model.getCode());
        dialog.dismiss();


    }


    public interface Listener {
        void onStep1Valid(ClientSignUpModel clientSignUpModel);
    }
}
