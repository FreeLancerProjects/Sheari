package com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.ProviderSignUpActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_verify_code.VerifyCodeActivity;
import com.creative.share.apps.sheari.adapters.CategorySpinnerAdapter;
import com.creative.share.apps.sheari.adapters.SubCategoryAdapter2;
import com.creative.share.apps.sheari.databinding.DialogYearsBinding;
import com.creative.share.apps.sheari.databinding.FragmentProviderStep4Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.PlaceGeocodeData;
import com.creative.share.apps.sheari.models.PlaceMapDetailsData;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Provider_Step4 extends Fragment implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG="OUT";
    private FragmentProviderStep4Binding binding;
    private ProviderSignUpActivity activity;
    private String lang;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private GoogleMap mMap;
    private double lat,lng;
    private Marker marker;
    private final float zoom = 15.6f;
    private Preferences preferences;
    private UserModel userModel;
    private FragmentMapTouchListener fragment;
    private Listeners.ProviderSteps listener = null;
    private int year = 0;
    private ProviderSignUpModel providerSignUpModel;
    private List<CategoryModel> spinnerAdList;
    private List<CategoryModel> spinnerCategoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter,adSpinnerAdapter;
    private List<Integer> sub_category_ids_list;
    private SubCategoryAdapter2 subCategoryAdapter2;
    private boolean out = false;


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (listener==null)
        {
            listener = (Listeners.ProviderSteps) context;
        }
    }

    public static Fragment_Provider_Step4 newInstance(boolean out) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,  out);
        Fragment_Provider_Step4 fragment_provider_step4 =new Fragment_Provider_Step4();
        fragment_provider_step4.setArguments(bundle);

        return fragment_provider_step4;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_step4, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        activity = (ProviderSignUpActivity) getActivity();
        preferences = Preferences.newInstance();
        sub_category_ids_list = new ArrayList<>();
        spinnerAdList = new ArrayList<>();
        spinnerAdList.add(new CategoryModel(getString(R.string.ads_department)));

        spinnerCategoryList = new ArrayList<>();
        spinnerCategoryList.add(new CategoryModel(getString(R.string.main_department)));

        providerSignUpModel = activity.getProviderSignUpModel();
        binding.setSignUpModel(providerSignUpModel);

        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        initMap();
        CheckPermission();

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            out = bundle.getBoolean(TAG);

        }

        categorySpinnerAdapter = new CategorySpinnerAdapter(activity,spinnerAdList);
        binding.spinnerAd.setAdapter(categorySpinnerAdapter);

        adSpinnerAdapter = new CategorySpinnerAdapter(activity,spinnerCategoryList);
        binding.spinnerCategory.setAdapter(adSpinnerAdapter);

        binding.rb1.setOnClickListener(view -> {
            providerSignUpModel.setService(1);
            binding.rb2.setChecked(false);
            binding.rb3.setChecked(false);



        });

        binding.rb2.setOnClickListener(view -> {
            providerSignUpModel.setService(2);

            binding.rb1.setChecked(false);
            binding.rb3.setChecked(false);

        });

        binding.rb3.setOnClickListener(view -> {
            providerSignUpModel.setService(3);

            binding.rb1.setChecked(false);
            binding.rb2.setChecked(false);

        });

        binding.imageSearch.setOnClickListener(view -> {
            String query = binding.edtAddress.getText().toString().trim();
            if (!TextUtils.isEmpty(query))
            {
                binding.edtAddress.setError(null);
                Search(query);
            }else
            {
                binding.edtAddress.setError(getString(R.string.field_req));

            }
        });
        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.tvYear.setOnClickListener((view -> CreateDateDialog()));
        binding.btnDone.setOnClickListener(view -> {

            if (providerSignUpModel.step4IsValid(activity))
            {
                if (providerSignUpModel.getType()==1)
                {
                    if (providerSignUpModel.getImage_uri()==null)
                    {
                        signUpClientWithoutImage();
                    }else
                        {
                            signUpClientWithImage();

                        }

                }else if (providerSignUpModel.getType()==2)
                {
                    if (providerSignUpModel.getImage_uri()==null)
                    {
                        signUpProviderWithoutImage();
                    }else
                    {
                        signUpProviderWithImage();
                    }
                }
            }
        });

        binding.spinnerAd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    providerSignUpModel.setAd_dept_id(0);
                }else
                    {

                        providerSignUpModel.setAd_dept_id(spinnerAdList.get(i).getId());

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
                    sub_category_ids_list.clear();
                    providerSignUpModel.setSub_dept_ids(sub_category_ids_list);

                }else
                {
                    int cat_id = spinnerCategoryList.get(i).getId();
                    getSubCategoryById(cat_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getAds();
        getCategory();
    }



    private void signUpClientWithoutImage() {

        String provider_type = "one";
        int map = 0;
        int delivery = 0;
        int charitable = 0;


        if (providerSignUpModel.getService()==1)
        {
            charitable = 1;


        }else if (providerSignUpModel.getService()==2)
        {
            delivery = 1;
        }else if (providerSignUpModel.getService()==3)
        {
            map = 1;
        }

        if (providerSignUpModel.getType()==2)
        {
            provider_type = "company";
        }

        RequestBody name_part = Common.getRequestBodyText(providerSignUpModel.getName());
        RequestBody email_part = Common.getRequestBodyText(providerSignUpModel.getEmail());
        RequestBody phone_part = Common.getRequestBodyText(providerSignUpModel.getPhone_code()+providerSignUpModel.getPhone());
        RequestBody region_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getRegion_id()));

        RequestBody password_part = Common.getRequestBodyText(providerSignUpModel.getPassword());
        RequestBody bio_part = Common.getRequestBodyText(providerSignUpModel.getAbout_me());


        RequestBody charitable_part = Common.getRequestBodyText(String.valueOf(charitable));
        RequestBody delivery_part = Common.getRequestBodyText(String.valueOf(delivery));
        RequestBody map_part = Common.getRequestBodyText(String.valueOf(map));
        RequestBody provider_type_part = Common.getRequestBodyText(provider_type);


        RequestBody ads_id_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getAd_dept_id()));

        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLng()));
        List<RequestBody> ids = getRequestBodyIds();


        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .clientProviderSignUpWithOutImage(lang,name_part,email_part,phone_part,region_part,password_part,bio_part,map_part,delivery_part,charitable_part,provider_type_part,ads_id_part,lat_part,lng_part,ids)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isStatus())
                                {
                                    if (response.body().getData().getIs_verified().equals("0"))
                                    {
                                        Intent intent = new Intent(activity, VerifyCodeActivity.class);
                                        intent.putExtra("data",response.body());
                                        intent.putExtra("out",out);
                                        startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        preferences.create_update_userData(activity,response.body());
                                        preferences.createSession(activity, Tags.session_login);

                                        if (!out)
                                        {
                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            startActivity(intent);

                                        }
                                        activity.finish();
                                    }


                                }else
                                {
                                    Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }


    }

    private void signUpClientWithImage() {

        String provider_type = "one";
        int map = 0;
        int delivery = 0;
        int charitable = 0;


        if (providerSignUpModel.getService()==1)
        {
            charitable = 1;


        }else if (providerSignUpModel.getService()==2)
        {
            delivery = 1;
        }else if (providerSignUpModel.getService()==3)
        {
            map = 1;
        }

        if (providerSignUpModel.getType()==2)
        {
            provider_type = "company";
        }

        RequestBody name_part = Common.getRequestBodyText(providerSignUpModel.getName());
        RequestBody email_part = Common.getRequestBodyText(providerSignUpModel.getEmail());
        RequestBody phone_part = Common.getRequestBodyText(providerSignUpModel.getPhone_code()+providerSignUpModel.getPhone());
        RequestBody region_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getRegion_id()));

        RequestBody password_part = Common.getRequestBodyText(providerSignUpModel.getPassword());
        RequestBody bio_part = Common.getRequestBodyText(providerSignUpModel.getAbout_me());


        RequestBody charitable_part = Common.getRequestBodyText(String.valueOf(charitable));
        RequestBody delivery_part = Common.getRequestBodyText(String.valueOf(delivery));
        RequestBody map_part = Common.getRequestBodyText(String.valueOf(map));
        RequestBody provider_type_part = Common.getRequestBodyText(provider_type);


        RequestBody ads_id_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getAd_dept_id()));

        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLng()));
        MultipartBody.Part image_part = Common.getMultiPart(activity,providerSignUpModel.getImage_uri(),"image");


        List<RequestBody> ids = getRequestBodyIds();



        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .clientProviderSignUpWithImage(lang,name_part,email_part,phone_part,region_part,password_part,bio_part,map_part,delivery_part,charitable_part,provider_type_part,ads_id_part,lat_part,lng_part,ids,image_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isStatus())
                                {
                                    if (response.body().getData().getIs_verified().equals("0"))
                                    {
                                        Intent intent = new Intent(activity, VerifyCodeActivity.class);
                                        intent.putExtra("data",response.body());
                                        intent.putExtra("out",out);
                                        startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        preferences.create_update_userData(activity,response.body());
                                        preferences.createSession(activity, Tags.session_login);

                                        if (!out)
                                        {
                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            startActivity(intent);

                                        }
                                        activity.finish();
                                    }


                                }else
                                {
                                    Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }

    }

    private void signUpProviderWithoutImage() {

        String provider_type = "one";
        int map = 0;
        int delivery = 0;
        int charitable = 0;


        if (providerSignUpModel.getService()==1)
        {
            charitable = 1;


        }else if (providerSignUpModel.getService()==2)
        {
            delivery = 1;
        }else if (providerSignUpModel.getService()==3)
        {
            map = 1;
        }

        if (providerSignUpModel.getType()==2)
        {
            provider_type = "company";
        }

        RequestBody type_part = Common.getRequestBodyText(providerSignUpModel.getCompany_type());
        RequestBody name_part = Common.getRequestBodyText(providerSignUpModel.getName());
        RequestBody email_part = Common.getRequestBodyText(providerSignUpModel.getEmail());
        RequestBody phone_part = Common.getRequestBodyText(providerSignUpModel.getPhone_code()+providerSignUpModel.getPhone());
        RequestBody region_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getRegion_id()));

        RequestBody password_part = Common.getRequestBodyText(providerSignUpModel.getPassword());
        RequestBody bio_part = Common.getRequestBodyText(providerSignUpModel.getAbout_me());


        RequestBody charitable_part = Common.getRequestBodyText(String.valueOf(charitable));
        RequestBody delivery_part = Common.getRequestBodyText(String.valueOf(delivery));
        RequestBody map_part = Common.getRequestBodyText(String.valueOf(map));
        RequestBody provider_type_part = Common.getRequestBodyText(provider_type);

        RequestBody employee_num_part = Common.getRequestBodyText(providerSignUpModel.getFrom_emp()+"-"+providerSignUpModel.getTo_emp());
        RequestBody creation_year_part = Common.getRequestBodyText(providerSignUpModel.getYear());
        RequestBody comercial_part = Common.getRequestBodyText(providerSignUpModel.getCommercial());



        RequestBody ads_id_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getAd_dept_id()));

        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLng()));
        List<RequestBody> ids = getRequestBodyIds();


        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .companyProviderSignUpWithOutImage(lang,name_part,email_part,phone_part,region_part,password_part,bio_part,map_part,delivery_part,charitable_part,provider_type_part,ads_id_part,lat_part,lng_part,employee_num_part,creation_year_part,comercial_part,type_part,ids)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isStatus())
                                {
                                    if (response.body().getData().getIs_verified().equals("0"))
                                    {
                                        Intent intent = new Intent(activity, VerifyCodeActivity.class);
                                        intent.putExtra("data",response.body());
                                        intent.putExtra("out",out);
                                        startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        preferences.create_update_userData(activity,response.body());
                                        preferences.createSession(activity, Tags.session_login);

                                        if (!out)
                                        {
                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            startActivity(intent);

                                        }
                                        activity.finish();
                                    }


                                }else
                                {
                                    Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }

    }

    private void signUpProviderWithImage() {

        String provider_type = "one";
        int map = 0;
        int delivery = 0;
        int charitable = 0;


        if (providerSignUpModel.getService()==1)
        {
            charitable = 1;


        }else if (providerSignUpModel.getService()==2)
        {
            delivery = 1;
        }else if (providerSignUpModel.getService()==3)
        {
            map = 1;
        }

        if (providerSignUpModel.getType()==2)
        {
            provider_type = "company";
        }

        RequestBody type_part = Common.getRequestBodyText(providerSignUpModel.getCompany_type());
        RequestBody name_part = Common.getRequestBodyText(providerSignUpModel.getName());
        RequestBody email_part = Common.getRequestBodyText(providerSignUpModel.getEmail());
        RequestBody phone_part = Common.getRequestBodyText(providerSignUpModel.getPhone_code()+providerSignUpModel.getPhone());
        RequestBody region_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getRegion_id()));

        RequestBody password_part = Common.getRequestBodyText(providerSignUpModel.getPassword());
        RequestBody bio_part = Common.getRequestBodyText(providerSignUpModel.getAbout_me());


        RequestBody charitable_part = Common.getRequestBodyText(String.valueOf(charitable));
        RequestBody delivery_part = Common.getRequestBodyText(String.valueOf(delivery));
        RequestBody map_part = Common.getRequestBodyText(String.valueOf(map));
        RequestBody provider_type_part = Common.getRequestBodyText(provider_type);

        RequestBody employee_num_part = Common.getRequestBodyText(providerSignUpModel.getFrom_emp()+"-"+providerSignUpModel.getTo_emp());
        RequestBody creation_year_part = Common.getRequestBodyText(providerSignUpModel.getYear());
        RequestBody comercial_part = Common.getRequestBodyText(providerSignUpModel.getCommercial());
        MultipartBody.Part image_part = Common.getMultiPart(activity,providerSignUpModel.getImage_uri(),"image");



        RequestBody ads_id_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getAd_dept_id()));

        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(providerSignUpModel.getLng()));
        List<RequestBody> ids = getRequestBodyIds();


        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .companyProviderSignUpWithImage(lang,name_part,email_part,phone_part,region_part,password_part,bio_part,map_part,delivery_part,charitable_part,provider_type_part,ads_id_part,lat_part,lng_part,employee_num_part,creation_year_part,comercial_part,type_part,ids,image_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isStatus())
                                {
                                    if (response.body().getData().getIs_verified().equals("0"))
                                    {
                                        Intent intent = new Intent(activity, VerifyCodeActivity.class);
                                        intent.putExtra("data",response.body());
                                        intent.putExtra("out",out);
                                        startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        preferences.create_update_userData(activity,response.body());
                                        preferences.createSession(activity, Tags.session_login);

                                        if (!out)
                                        {
                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            startActivity(intent);

                                        }
                                        activity.finish();
                                    }


                                }else
                                {
                                    Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }
    }


    private List<RequestBody> getRequestBodyIds()
    {
        List<RequestBody> requestBodyList = new ArrayList<>();

        for (Integer id :sub_category_ids_list)
        {
            RequestBody requestBody = Common.getRequestBodyText(String.valueOf(id));
            requestBodyList.add(requestBody);
        }
        return requestBodyList;
    }
    private void getAds() {
        Api.getService(Tags.base_url)
                .getAds(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {


                                spinnerAdList.clear();
                                spinnerAdList.add(new CategoryModel(getString(R.string.ads_department)));
                                spinnerAdList.addAll(response.body().getData());
                                activity.runOnUiThread(() -> adSpinnerAdapter.notifyDataSetChanged());

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

    private void getCategory() {

        Api.getService(Tags.base_url)
                .getCategory(lang)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                spinnerCategoryList.clear();
                                spinnerCategoryList.add(new CategoryModel(getString(R.string.main_department)));
                                spinnerCategoryList.addAll(response.body().getData());
                                activity.runOnUiThread(() -> categorySpinnerAdapter.notifyDataSetChanged());

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

    private void getSubCategoryById(int cat_id)
    {

        Api.getService(Tags.base_url)
                .getSubCategory(lang,cat_id)
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                updateAdapterData(response.body().getData());
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

    private void updateAdapterData(List<CategoryModel> data) {

        if (data.size()>0)
        {

            subCategoryAdapter2 = new SubCategoryAdapter2(data,activity,sub_category_ids_list,this);
            binding.recView.setLayoutManager(new LinearLayoutManager(activity));
            binding.recView.setAdapter(subCategoryAdapter2);
        }
    }

    private void CreateDateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .create();

        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);


        DialogYearsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_years, null, false);
        binding.npd.setMaxValue(this.year);
        binding.npd.setMinValue(this.year-50);
        binding.npd.setWrapSelectorWheel(false);
        binding.npd.setValue(this.year);
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );

        binding.npd.setOnValueChangedListener((numberPicker, i, i1) -> {
            this.year = i1;
        });
        binding.btnSelect.setOnClickListener((view ->
                {
                    this.binding.tvYear.setText(String.valueOf(this.year));
                    dialog.dismiss();
                })
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 550;
        lp.height = 850;
        dialog.getWindow().setAttributes(lp);

    }

    private void initMap()
    {

        fragment = (FragmentMapTouchListener) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }
    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(activity,fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{fineLocPerm}, loc_req);
        } else {

            initGoogleApi();
        }
    }
    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);

            mMap.setOnMapClickListener(latLng -> {
                marker.setPosition(latLng);
                lat = latLng.latitude;
                lng = latLng.longitude;
                getGeoData(lat,lng);
            });

            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

        }
    }

    private void AddMarker(double lat, double lng) {

        IconGenerator iconGenerator = new IconGenerator(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.marker_bg,null);
        iconGenerator.setBackground(null);
        iconGenerator.setContentView(view);

        this.lat = lat;
        this.lng = lng;
        providerSignUpModel.setLat(lat);
        providerSignUpModel.setLng(lng);
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        } else {
            marker.setPosition(new LatLng(lat, lng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        }


    }

    private void getGeoData(final double lat, double lng) {

        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getResults().size() > 0) {
                                String address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtAddress.setText(address);
                                /*addCompanyModel.setAddress(address);
                                binding.setAddCompanyModel(addCompanyModel);
                                */


                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {


                            // Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void Search(String query) {

        String fields = "id,place_id,name,geometry,formatted_address";
        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getCandidates().size() > 0) {
                                String address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                lat = response.body().getCandidates().get(0).getGeometry().getLocation().getLat();
                                lng = response.body().getCandidates().get(0).getGeometry().getLocation().getLng();
                                providerSignUpModel.setAddress(address);


                                AddMarker(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(), response.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                            }
                        } else {


                            try {
                                Log.e("error_code", response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {


                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(activity,100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(activity)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        AddMarker(lat,lng);
        getGeoData(lat,lng);

        if (googleApiClient!=null)
        {
            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGoogleApi();
            }else
            {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100&&resultCode== Activity.RESULT_OK)
        {

            startLocationUpdate();
        }
    }


    public void removeItem(int id) {

        if (sub_category_ids_list.size()>0)
        {
            int index = getItemId(id);
            if (index!=-1)
            {
                sub_category_ids_list.remove(index);

            }
        }


    }

    public void addItem(CategoryModel model) {
        sub_category_ids_list.add(model.getId());
        providerSignUpModel.setSub_dept_ids(sub_category_ids_list);



    }

    private int getItemId(int item_id)
    {
        for (int index=0;index<sub_category_ids_list.size();index++)
        {
            if (item_id==sub_category_ids_list.get(index))
            {
                return index;
            }
        }

        return -1;
    }

}
