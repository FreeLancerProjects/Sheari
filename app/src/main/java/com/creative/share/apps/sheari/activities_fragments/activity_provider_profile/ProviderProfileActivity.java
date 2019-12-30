package com.creative.share.apps.sheari.activities_fragments.activity_provider_profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_make_order.FragmentMapTouchListener;
import com.creative.share.apps.sheari.adapters.MyFieldAdapter2;
import com.creative.share.apps.sheari.databinding.ActivityProviderProfileBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.Locale;

import io.paperdb.Paper;

public class ProviderProfileActivity extends AppCompatActivity implements Listeners.BackListener , OnMapReadyCallback{
    private ActivityProviderProfileBinding binding;
    private String lang;
    private GoogleMap mMap;
    private float zoom = 15.6f;
    private FragmentMapTouchListener fragment;
    private MyFieldAdapter2 adapter;
    private LinearLayoutManager manager;
    private Preferences preferences;
    private UserModel userModel;

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
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(userModel);

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
        initMap();




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
