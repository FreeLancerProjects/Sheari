package com.creative.share.apps.sheari.activities_fragments.activity_provider_profile;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_make_order.FragmentMapTouchListener;
import com.creative.share.apps.sheari.adapters.MyFieldAdapter2;
import com.creative.share.apps.sheari.adapters.ProjectAdapter;
import com.creative.share.apps.sheari.databinding.ActivityProviderProfileBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ProjectDataModel;
import com.creative.share.apps.sheari.models.ProjectModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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

public class ProviderProfileActivity extends AppCompatActivity implements Listeners.BackListener , OnMapReadyCallback{
    private ActivityProviderProfileBinding binding;
    private String lang;
    private GoogleMap mMap;
    private float zoom = 15.6f;
    private FragmentMapTouchListener fragment;
    private MyFieldAdapter2 adapter;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_provider_profile);
        initView();
    }



    private void initView() {
        projectModelList = new ArrayList<>();

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(userModel);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBar2.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.recView.setLayoutManager(manager);
        adapter = new MyFieldAdapter2(userModel.getData().getSub_categories(),this);
        binding.recView.setAdapter(adapter);

        if (userModel.getData().getSub_categories().size()>0)
        {
            binding.tvNoField.setVisibility(View.GONE);
        }else
            {
                binding.tvNoField.setVisibility(View.VISIBLE);

            }

        manager2 = new LinearLayoutManager(this);
        binding.recView2.setLayoutManager(manager2);
        projectAdapter = new ProjectAdapter(projectModelList,this,false);
        binding.recView2.setAdapter(projectAdapter);

        getAllProject();


        initMap();




    }


    private void getAllProject() {

        Api.getService(Tags.base_url)
                .getAllProjects("Bearer "+userModel.getData().getToken())
                .enqueue(new Callback<ProjectDataModel>() {
                    @Override
                    public void onResponse(Call<ProjectDataModel> call, Response<ProjectDataModel> response) {
                        binding.progBar2.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                projectModelList.clear();
                                projectModelList.addAll(response.body().getData());
                                if (projectModelList.size()>0)
                                {
                                    binding.tvNoWorks.setVisibility(View.GONE);
                                    projectAdapter.notifyDataSetChanged();

                                }else
                                {
                                    binding.tvNoWorks.setVisibility(View.VISIBLE);

                                }


                            } else {
                                Toast.makeText(ProviderProfileActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProviderProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ProviderProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectDataModel> call, Throwable t) {
                        try {
                            binding.progBar2.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProviderProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProviderProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void initMap() {
        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment!=null)
        {
            fragment.getMapAsync(this);

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

            AddMarker(Double.parseDouble(userModel.getData().getLat()),Double.parseDouble(userModel.getData().getLng()),userModel.getData().getCountry_name()+"-"+userModel.getData().getCity_name()+"-"+userModel.getData().getRegion_name());
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




    @Override
    public void back() {
        finish();
    }
}
