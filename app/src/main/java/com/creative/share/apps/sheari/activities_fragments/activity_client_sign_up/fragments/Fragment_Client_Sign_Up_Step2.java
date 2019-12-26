package com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments;

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
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.ClientSignUpActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_terms.TermsActivity;
import com.creative.share.apps.sheari.databinding.FragmentClientSignUpStep2Binding;
import com.creative.share.apps.sheari.models.ClientSignUpModel;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Client_Sign_Up_Step2 extends Fragment {
    private static final String TAG = "DATA";
    private static final String TAG2 = "OUT";

    private FragmentClientSignUpStep2Binding binding;
    private ClientSignUpActivity activity;
    private String lang;
    private Preferences preferences;
    private ClientSignUpModel clientSignUpModel;
    private boolean out = false;



    public static Fragment_Client_Sign_Up_Step2 newInstance(ClientSignUpModel clientSignUpModel, boolean out)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,  clientSignUpModel);
        bundle.putBoolean(TAG2,out);
        Fragment_Client_Sign_Up_Step2 fragment_client_sign_up_step2 =new Fragment_Client_Sign_Up_Step2();
        fragment_client_sign_up_step2.setArguments(bundle);

        return fragment_client_sign_up_step2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_sign_up_step2, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        activity = (ClientSignUpActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            clientSignUpModel = (ClientSignUpModel) bundle.getSerializable(TAG);
            binding.setClientSignUp(clientSignUpModel);
            out = bundle.getBoolean(TAG2);

        }

        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.btnDone.setOnClickListener((view -> {

            if (clientSignUpModel.step2IsValid(activity))
            {
                signUp();
            }
        }));
        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                clientSignUpModel.setAcceptTerms(true);
                Intent intent = new Intent(activity, TermsActivity.class);
                startActivity(intent);
            }else
                {
                    clientSignUpModel.setAcceptTerms(false);

                }



        });



    }

    private void signUp() {


    }


}
