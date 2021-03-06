package com.creative.share.apps.sheari.activities_fragments.activity_make_order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_make_order2.MakeOrder2Activity;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.adapters.MyFieldAdapter;
import com.creative.share.apps.sheari.adapters.ProjectAdapter;
import com.creative.share.apps.sheari.databinding.ActivityCreateOrderBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ProjectModel;
import com.creative.share.apps.sheari.models.ProviderModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CreateOrderActivity extends AppCompatActivity implements Listeners.BackListener , OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private ActivityCreateOrderBinding binding;
    private String lang;
    private Marker marker;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private double lat = 0.0, lng = 0.0;
    private float zoom = 15.6f;
    private String address="";
    private FragmentMapTouchListener fragment;
    private int cat_id;
    private ProviderModel providerModel=null;
    private MyFieldAdapter adapter;
    private LinearLayoutManager manager,manager2;
    private Preferences preferences;
    private UserModel userModel;
    private ProjectAdapter projectAdapter;
    private List<ProjectModel> projectModelList;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (providerModel!=null)
        {
            updateUi(providerModel);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_order);
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
        projectModelList = new ArrayList<>();

        preferences = Preferences.newInstance();

        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);






        binding.btnNext.setOnClickListener(view -> {
            userModel = preferences.getUserData(this);

            if (userModel==null)
            {
                Intent intent = new Intent(this, SignInActivity.class);
                intent.putExtra("from",true);
                startActivity(intent);
            }else
                {
                    Intent intent = new Intent(this, MakeOrder2Activity.class);
                    intent.putExtra("cat_id",cat_id);
                    intent.putExtra("data",providerModel);
                    startActivity(intent);
                }


        });
        initMap();




    }

    private void updateUi(ProviderModel providerModel) {
        binding.setModel(providerModel);
        userModel = preferences.getUserData(this);
        if (userModel!=null){

            if (userModel.getData().getRole().equals("provider"))
            {
                binding.btnNext.setVisibility(View.GONE);
            }else
                {
                    binding.btnNext.setVisibility(View.VISIBLE);

                }
        }else
            {
                binding.btnNext.setVisibility(View.GONE);
            }
        if (this.providerModel.getSub_categories().size()>0)
        {
            adapter = new MyFieldAdapter(providerModel.getSub_categories(),this);
            manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            binding.recView.setLayoutManager(manager);
            binding.recView.setAdapter(adapter);
        }else
        {
            binding.tvNoField.setVisibility(View.VISIBLE);
        }



    }

    private void initMap() {
        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment!=null)
        {
            fragment.getMapAsync(this);

        }
    }




    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{fineLocPerm}, loc_req);
        } else {

            initGoogleApi();
        }
    }

    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
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
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
           // CheckPermission();
           /* mMap.setOnMapClickListener(latLng -> {
                marker.setPosition(latLng);
                lat = latLng.latitude;
                lng = latLng.longitude;
               // getGeoData(lat, lng);
            });*/

            if (providerModel!=null&&providerModel.getLat()!=null&&!providerModel.getLat().isEmpty()&&providerModel.getLng()!=null&&!providerModel.getLng().isEmpty())
            {

                String address = providerModel.getName()+"/"+providerModel.getCountry_name()+"-"+providerModel.getCity_name()+"-"+providerModel.getRegion_name();


                AddMarker(Double.parseDouble(providerModel.getLat()),Double.parseDouble(providerModel.getLng()),address);

            }

            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

        }
    }

    private void AddMarker(double lat, double lng, String address) {



        View view = LayoutInflater.from(this).inflate(R.layout.marker_bg,null);
        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),zoom));

    }

  /*  private void getGeoData(final double lat, double lng) {

        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getResults().size() > 0) {
                                address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtSearch.setText(address);
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
                                address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                lat = response.body().getCandidates().get(0).getGeometry().getLocation().getLat();
                                lng = response.body().getCandidates().get(0).getGeometry().getLocation().getLng();
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
    }*/

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
                        status.startResolutionForResult(this, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        AddMarker(lat, lng, address);
        //getGeoData(lat, lng);
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        googleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null) {
            if (locationCallback!=null)
            {
                LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
                googleApiClient.disconnect();
                googleApiClient = null;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleApi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK)
        {
            startLocationUpdate();

        }
    }

    @Override
    public void back() {
        finish();
    }
}
