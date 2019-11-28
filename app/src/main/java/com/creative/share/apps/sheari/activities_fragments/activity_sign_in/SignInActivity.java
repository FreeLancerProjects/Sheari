package com.creative.share.apps.sheari.activities_fragments.activity_sign_in;

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
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.fragments.Fragment_Chooser;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.fragments.Fragment_Sign_In;
import com.creative.share.apps.sheari.databinding.ActivitySignInBinding;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private int fragment_count = 0;
    private FragmentManager manager;
    private Preferences preferences;
    private Fragment_Chooser fragment_chooser;
    private Fragment_Sign_In fragment_sign_in;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        manager = getSupportFragmentManager();
        preferences = Preferences.newInstance();
        if (savedInstanceState == null) {
            displayFragmentChooser();
        }

    }



    public void displayFragmentChooser() {
        fragment_count++;
        fragment_chooser = Fragment_Chooser.newInstance();

        manager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_chooser, "fragment_chooser").addToBackStack("fragment_chooser").commit();

    }

    public void displayFragmentSignIn() {
        fragment_count++;
        fragment_sign_in = Fragment_Sign_In.newInstance();

        manager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_sign_in, "fragment_sign_in").addToBackStack("fragment_sign_in").commit();

    }



    public void refreshActivity(String lang) {
        Paper.init(this);
        Paper.book().write("lang", lang);
        preferences.selectedLanguage(this, lang);
        preferences.saveSelectedLanguage(this);
        LanguageHelper.setNewLocale(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);

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
            finish();
        }
    }
}
