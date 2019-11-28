package com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.ProviderSignUpActivity;
import com.creative.share.apps.sheari.databinding.FragmentProviderStep1Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Provider_Step1 extends Fragment {

    private FragmentProviderStep1Binding binding;
    private ProviderSignUpActivity activity;
    private String lang;
    private Listeners.ProviderSteps listener = null;
    private ProviderSignUpModel providerSignUpModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (listener==null)
        {
            listener = (Listeners.ProviderSteps) context;
        }
    }

    public static Fragment_Provider_Step1 newInstance() {
        return new Fragment_Provider_Step1();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_step1, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {

        activity = (ProviderSignUpActivity) getActivity();
        providerSignUpModel = activity.getProviderSignUpModel();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.btnNext.setOnClickListener((view ->{
            if (providerSignUpModel.step1IsValid(activity))
            {
                activity.setProviderSignUpModel(providerSignUpModel);
                listener.step2();
            }


        }
        ));
        binding.rbUser.setOnClickListener(view -> {
            providerSignUpModel.setType(1);
            binding.rbCompany.setChecked(false);
        });
        binding.rbCompany.setOnClickListener(view -> {
            providerSignUpModel.setType(2);
            binding.rbUser.setChecked(false);
        });

    }




}
