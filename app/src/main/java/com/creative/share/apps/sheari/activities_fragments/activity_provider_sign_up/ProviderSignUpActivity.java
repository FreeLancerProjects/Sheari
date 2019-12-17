package com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step1;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step2;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step3;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step4;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.databinding.ActivityProviderSignUpBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.List;

import io.paperdb.Paper;

public class ProviderSignUpActivity extends AppCompatActivity  implements Listeners.ProviderSteps {
    private ActivityProviderSignUpBinding binding;
    private int fragment_count = 0;
    private FragmentManager manager;
    private Preferences preferences;
    private Fragment_Provider_Step1 fragment_provider_step1;
    private Fragment_Provider_Step2  fragment_provider_step2;
    private Fragment_Provider_Step3 fragment_provider_step3;
    private Fragment_Provider_Step4 fragment_provider_step4;
    private ProviderSignUpModel providerSignUpModel;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_provider_sign_up);
        manager = getSupportFragmentManager();
        preferences = Preferences.newInstance();
        providerSignUpModel = new ProviderSignUpModel();
        if (savedInstanceState == null) {
            displayFragmentStep1();
        }

    }




    private void displayFragmentStep1() {
        fragment_count++;
        fragment_provider_step1 = Fragment_Provider_Step1.newInstance();

        manager.beginTransaction().add(R.id.fragment_provider_sign_up_container, fragment_provider_step1, "fragment_provider_step1").addToBackStack("fragment_provider_step1").commit();

        updateStep1UI();
    }
    private void displayFragmentStep2() {
        fragment_count++;
        fragment_provider_step2 = Fragment_Provider_Step2.newInstance();

        manager.beginTransaction().add(R.id.fragment_provider_sign_up_container, fragment_provider_step2, "fragment_provider_step2").addToBackStack("fragment_provider_step2").commit();

        updateStep2UI();

    }
    private void displayFragmentStep3() {
        fragment_count++;
        fragment_provider_step3 = Fragment_Provider_Step3.newInstance();

        manager.beginTransaction().add(R.id.fragment_provider_sign_up_container, fragment_provider_step3, "fragment_provider_step3").addToBackStack("fragment_provider_step3").commit();

        updateStep3UI();

    }
    private void displayFragmentStep4() {
        fragment_count++;
        fragment_provider_step4 = Fragment_Provider_Step4.newInstance();

        manager.beginTransaction().add(R.id.fragment_provider_sign_up_container, fragment_provider_step4, "fragment_provider_step4").addToBackStack("fragment_provider_step4").commit();

        updateStep4UI();

    }

    private void updateStep1UI()
    {
        binding.tv1.setVisibility(View.VISIBLE);
        binding.tv1.setTextColor(ContextCompat.getColor(this,R.color.white));
        binding.tv1.setBackgroundResource(R.drawable.circle_green);
        binding.img1.setVisibility(View.GONE);

        binding.tv2.setVisibility(View.VISIBLE);
        binding.tv2.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv2.setBackgroundResource(R.drawable.circle_gray);
        binding.img2.setVisibility(View.GONE);


        binding.tv3.setVisibility(View.VISIBLE);
        binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv3.setBackgroundResource(R.drawable.circle_gray);
        binding.img3.setVisibility(View.GONE);

        binding.tv4.setVisibility(View.VISIBLE);
        binding.tv4.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv4.setBackgroundResource(R.drawable.circle_gray);
        binding.img4.setVisibility(View.GONE);

    }

    private void updateStep2UI()
    {

        binding.tv1.setVisibility(View.GONE);
        binding.img1.setVisibility(View.VISIBLE);

        binding.tv2.setVisibility(View.VISIBLE);
        binding.tv2.setTextColor(ContextCompat.getColor(this,R.color.white));
        binding.tv2.setBackgroundResource(R.drawable.circle_green);
        binding.img2.setVisibility(View.GONE);


        binding.tv3.setVisibility(View.VISIBLE);
        binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv3.setBackgroundResource(R.drawable.circle_gray);
        binding.img3.setVisibility(View.GONE);

        binding.tv4.setVisibility(View.VISIBLE);
        binding.tv4.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv4.setBackgroundResource(R.drawable.circle_gray);
        binding.img4.setVisibility(View.GONE);

    }

    private void updateStep3UI()
    {

        binding.tv1.setVisibility(View.GONE);
        binding.img1.setVisibility(View.VISIBLE);

        binding.tv2.setVisibility(View.GONE);
        binding.img2.setVisibility(View.VISIBLE);


        binding.tv3.setVisibility(View.VISIBLE);
        binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.white));
        binding.tv3.setBackgroundResource(R.drawable.circle_green);
        binding.img3.setVisibility(View.GONE);

        binding.tv4.setVisibility(View.VISIBLE);
        binding.tv4.setTextColor(ContextCompat.getColor(this,R.color.gray4));
        binding.tv4.setBackgroundResource(R.drawable.circle_gray);
        binding.img4.setVisibility(View.GONE);
    }

    private void updateStep4UI()
    {

        binding.tv1.setVisibility(View.GONE);
        binding.img1.setVisibility(View.VISIBLE);

        binding.tv2.setVisibility(View.GONE);
        binding.img2.setVisibility(View.VISIBLE);


        binding.tv3.setVisibility(View.GONE);
        binding.img3.setVisibility(View.VISIBLE);

        binding.tv4.setVisibility(View.VISIBLE);
        binding.tv4.setTextColor(ContextCompat.getColor(this,R.color.white));
        binding.tv4.setBackgroundResource(R.drawable.circle_green);
        binding.img4.setVisibility(View.GONE);

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
            if (fragment_count ==3)
            {
                updateStep3UI();
            }else if (fragment_count ==2)
            {
                updateStep2UI();
            }else if (fragment_count==1)
            {
                updateStep1UI();
            }
            super.onBackPressed();
        } else {

            Intent intent= new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public ProviderSignUpModel getProviderSignUpModel()
    {
        return this.providerSignUpModel;
    }
    public void setProviderSignUpModel (ProviderSignUpModel providerSignUpModel)
    {
        this.providerSignUpModel = providerSignUpModel;
    }

    @Override
    public void step1() {
        displayFragmentStep1();
    }

    @Override
    public void step2() {
        displayFragmentStep2();
    }

    @Override
    public void step3() {
        displayFragmentStep3();
    }

    @Override
    public void step4() {
        displayFragmentStep4();
    }
}
