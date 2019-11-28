package com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.ClientSignUpActivity;
import com.creative.share.apps.sheari.databinding.FragmentClientSignUpStep1Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.ClientSignUpModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Client_Sign_Up_Step1 extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener {
    private FragmentClientSignUpStep1Binding binding;
    private ClientSignUpActivity activity;
    private String lang;
    private Preferences preferences;
    private CountryPicker countryPicker;
    private ClientSignUpModel clientSignUpModel;
    private Listener listener = null;


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (listener==null)
        {
            listener = (Listener) context;

        }
    }

    public static Fragment_Client_Sign_Up_Step1 newInstance() {
        return new Fragment_Client_Sign_Up_Step1();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_sign_up_step1, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        clientSignUpModel = new ClientSignUpModel();
        preferences = Preferences.newInstance();
        activity = (ClientSignUpActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setShowCountryDialogListener(this);
        binding.setClientSignUp(clientSignUpModel);
        createCountryDialog();

        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.btnNext.setOnClickListener((view ->
        {

            if (clientSignUpModel.step1IsValid(activity))
            {
                listener.onStep1Valid(clientSignUpModel);
            }

        }
                ));

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

        Log.e("ddd",country.getDialCode()+"__");
    }


    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }


    public interface Listener
    {
        void onStep1Valid(ClientSignUpModel clientSignUpModel);
    }
}
