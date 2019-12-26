package com.creative.share.apps.sheari.activities_fragments.activity_sign_in.fragments;

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
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.ProviderSignUpActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.databinding.FragmentChooserBinding;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Chooser extends Fragment {
    private static final String TAG="OUT";
    private FragmentChooserBinding binding;
    private SignInActivity activity;
    private String lang;
    private boolean out = false;


    public static Fragment_Chooser newInstance(boolean out) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(TAG,out);
        Fragment_Chooser fragment_chooser = new Fragment_Chooser();
        fragment_chooser.setArguments(bundle);
        return fragment_chooser;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chooser, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);


        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            out = bundle.getBoolean(TAG);
        }

        binding.flChangeLanguage.setOnClickListener((view) -> {

                    if (lang.equals("ar")) {
                        activity.refreshActivity("en");
                    } else {
                        activity.refreshActivity("ar");

                    }

                }
        );

        binding.btnSignIn.setOnClickListener((view) ->activity.displayFragmentSignIn()
        );

        binding.llClient.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ClientSignUpActivity.class);
            intent.putExtra("from",out);
            startActivity(intent);
            activity.finish();
        });


        binding.llProvider.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ProviderSignUpActivity.class);
            intent.putExtra("from",out);
            startActivity(intent);
            activity.finish();
        });
    }


}
