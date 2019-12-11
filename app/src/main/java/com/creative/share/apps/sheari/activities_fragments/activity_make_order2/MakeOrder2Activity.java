package com.creative.share.apps.sheari.activities_fragments.activity_make_order2;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ActivityMakeOrder2Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;

import java.util.Locale;

import io.paperdb.Paper;

public class MakeOrder2Activity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMakeOrder2Binding binding;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_make_order2 );
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


    }

    @Override
    public void back() {
        finish();
    }
}
