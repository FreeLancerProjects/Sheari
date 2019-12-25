package com.creative.share.apps.sheari.activities_fragments.activity_home.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.FragmentMapTouchListener;
import com.creative.share.apps.sheari.adapters.CategorySpinnerAdapter;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.databinding.FragmentSearchBinding;
import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CategoryModel;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.models.ProvidersDataModel;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Search extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private FragmentSearchBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private GoogleMap mMap;
    private double lat,lng;
    private Marker marker;
    private final float zoom = 15.6f;
    private FragmentMapTouchListener fragment;
    private List<LocationModel> cityList, countryList;
    private LocationSpinnerAdapter citySpinnerAdapter, countrySpinnerAdapter;
    private List<CategoryModel> spinnerCategoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private List<ProvidersDataModel.ProviderModel> providerModelList;
    private int current_page=1;
    private int cat_id=0,country_id=0,city_id=0;
    private ProgressDialog dialog;


    public static Fragment_Search newInstance() {
        return new Fragment_Search();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        providerModelList = new ArrayList<>();
        countryList = new ArrayList<>();
        cityList = new ArrayList<>();
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));
        spinnerCategoryList = new ArrayList<>();
        spinnerCategoryList.add(new CategoryModel(getString(R.string.dept2)));

        preferences = Preferences.newInstance();

        initMap();
        CheckPermission();


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
                        city_id = 0;
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
                        cat_id = 0;


                    }else
                    {
                        cat_id = spinnerCategoryList.get(i).getId();
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
            if (country_id!=0&&city_id!=0&&cat_id!=0)
            {
                dialog.show();
                getProviders(cat_id,country_id,city_id,current_page);
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

                    if (cat_id==0)
                    {
                        Toast.makeText(activity, getString(R.string.ch_dept), Toast.LENGTH_SHORT).show();
                    }
                }
        });


    }


    private void getData()
    {
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
    private void getCountry()
    {
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
    public void onMapReady(GoogleMap googleMap)
    {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);


            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

            getData();
            getCountry();


        }
    }



    private void getProviders(int cat_id,int country_id,int city_id,int page) {
        Api.getService(Tags.base_url)
                .getProvidersSearch(cat_id,country_id,city_id,page)
                .enqueue(new Callback<ProvidersDataModel>() {
                    @Override
                    public void onResponse(Call<ProvidersDataModel> call, Response<ProvidersDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                if (current_page==1)
                                {
                                    providerModelList.clear();

                                }
                                providerModelList.addAll(response.body().getData().getProviders());

                                if (response.body().getData().getPaginate().getTotal_pages()>=3&&current_page<=3)
                                {
                                    current_page++;
                                    getProviders(cat_id,country_id,city_id,current_page);
                                }else
                                    {
                                        if (providerModelList.size() > 0) {

                                            setUpMarkers();

                                        }else
                                            {
                                                if(mMap!=null)
                                                {
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

                                                }

                                                Toast.makeText(activity, getString(R.string.no_data_to_show), Toast.LENGTH_SHORT).show();

                                            }

                                    }



                            } else {
                                Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            dialog.dismiss();
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
                    public void onFailure(Call<ProvidersDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
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

    private void setUpMarkers() {


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (ProvidersDataModel.ProviderModel providerModel: providerModelList)
        {
            double lat = Double.parseDouble(providerModel.getLat());
            double lng = Double.parseDouble(providerModel.getLng());
            AddMarker(lat,lng,providerModel.getName());
            builder.include(new LatLng(lat,lng));
        }

        if (mMap!=null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));

        }
        dialog.dismiss();

    }


    private void AddMarker(double lat, double lng,String title)
    {

        View view = LayoutInflater.from(activity).inflate(R.layout.marker_bg,null);
        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));



    }
    private void AddMarkerMyLocation(double lat, double lng,String title)
    {

        View view = LayoutInflater.from(activity).inflate(R.layout.marker_bg,null);
        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),zoom));


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
        AddMarkerMyLocation(lat,lng,"My location");

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


}
