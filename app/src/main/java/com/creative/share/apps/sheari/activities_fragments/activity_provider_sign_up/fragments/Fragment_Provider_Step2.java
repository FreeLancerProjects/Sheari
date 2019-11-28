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
import com.creative.share.apps.sheari.databinding.FragmentProviderStep2Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Provider_Step2 extends Fragment {

    private FragmentProviderStep2Binding binding;
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

        activity = (ProviderSignUpActivity) getActivity();
        providerSignUpModel = activity.getProviderSignUpModel();
        binding.setSignUpModel(providerSignUpModel);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

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

    }


}
