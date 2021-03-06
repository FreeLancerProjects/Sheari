package com.creative.share.apps.sheari.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_offers.OffersActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_order.OrderActivity;
import com.creative.share.apps.sheari.databinding.FragmentMarketBinding;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Market extends Fragment {
    private FragmentMarketBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;

    public static Fragment_Market newInstance() {
        return new Fragment_Market();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_market, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());


        binding.flOrder.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OrderActivity.class);
            startActivity(intent);
        });

        binding.flOffer.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OffersActivity.class);
            startActivity(intent);
        });

    }


}
