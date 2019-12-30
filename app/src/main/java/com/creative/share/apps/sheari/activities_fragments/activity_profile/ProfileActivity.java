package com.creative.share.apps.sheari.activities_fragments.activity_profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.update_client_profile.UpdateClientProfileActivity;
import com.creative.share.apps.sheari.databinding.ActivityProfileBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity implements Listeners.BackListener {

    private ActivityProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isUpdated = false;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUserModel(userModel);
        binding.imageUpdate.setOnClickListener(view ->
        {
            Intent intent = new Intent(this, UpdateClientProfileActivity.class);
            startActivityForResult(intent,100);

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK&&data!=null)
        {
            isUpdated = true;
        }
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    public void back() {
        Back();
    }

    private void Back()
    {
        if (isUpdated)
        {
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);

        }

        finish();
    }
}
