package com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments.Fragment_Client_Sign_Up_Step1;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments.Fragment_Client_Sign_Up_Step2;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.databinding.ActivityClientSignUpBinding;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ClientSignUpModel;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.List;

import io.paperdb.Paper;

public class ClientSignUpActivity extends AppCompatActivity implements Fragment_Client_Sign_Up_Step1.Listener {
    private ActivityClientSignUpBinding binding;
    private int fragment_count = 0;
    private FragmentManager manager;
    private Preferences preferences;
    private Fragment_Client_Sign_Up_Step1 fragment_client_sign_up_step1;
    private Fragment_Client_Sign_Up_Step2 fragment_client_sign_up_step2;
    private boolean out = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_sign_up);
        manager = getSupportFragmentManager();
        preferences = Preferences.newInstance();
        if (savedInstanceState == null) {
            displayFragmentStep1();
        }
        getDataFromIntent();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("from")) {
            out = true;
        }
    }


    private void displayFragmentStep1() {
        fragment_count++;
        fragment_client_sign_up_step1 = Fragment_Client_Sign_Up_Step1.newInstance();

        manager.beginTransaction().add(R.id.fragment_client_sign_up_container, fragment_client_sign_up_step1, "fragment_client_sign_up_step1").addToBackStack("fragment_client_sign_up_step1").commit();

    }

    private void displayFragmentStep2(ClientSignUpModel clientSignUpModel) {
        fragment_count++;
        fragment_client_sign_up_step2 = Fragment_Client_Sign_Up_Step2.newInstance(clientSignUpModel,out);

        manager.beginTransaction().add(R.id.fragment_client_sign_up_container, fragment_client_sign_up_step2, "fragment_client_sign_up_step2").addToBackStack("fragment_client_sign_up_step2").commit();

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = manager.getFragments();
        for (Fragment fragment : fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = manager.getFragments();
        for (Fragment fragment : fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        if (fragment_count > 1) {
            fragment_count--;
            super.onBackPressed();
        } else {
            Intent intent= new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStep1Valid(ClientSignUpModel clientSignUpModel) {
        displayFragmentStep2(clientSignUpModel);
    }
}
