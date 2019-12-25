package com.creative.share.apps.sheari.activities_fragments.activity_payment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ActivityPaymentBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;

import java.util.Locale;

import io.paperdb.Paper;

public class PaymentActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityPaymentBinding binding;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


        binding.card1.setOnClickListener(view -> {

            binding.rb1.setChecked(true);
            binding.rb1.setVisibility(View.VISIBLE);

            binding.rb2.setChecked(false);
            binding.rb2.setVisibility(View.INVISIBLE);

            binding.rb3.setChecked(false);
            binding.rb3.setVisibility(View.INVISIBLE);


            binding.tv1.setTextColor(ContextCompat.getColor(this,R.color.black));
            binding.tv2.setTextColor(ContextCompat.getColor(this,R.color.gray4));
            binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.gray4));



        });

        binding.card2.setOnClickListener(view -> {
            binding.rb1.setChecked(false);
            binding.rb1.setVisibility(View.INVISIBLE);

            binding.rb2.setChecked(true);
            binding.rb2.setVisibility(View.VISIBLE);

            binding.rb3.setChecked(false);
            binding.rb3.setVisibility(View.INVISIBLE);


            binding.tv1.setTextColor(ContextCompat.getColor(this,R.color.gray4));
            binding.tv2.setTextColor(ContextCompat.getColor(this,R.color.black));
            binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.gray4));


        });

        binding.card3.setOnClickListener(view -> {
            binding.rb1.setChecked(false);
            binding.rb2.setVisibility(View.INVISIBLE);

            binding.rb2.setChecked(false);
            binding.rb2.setVisibility(View.INVISIBLE);

            binding.rb3.setChecked(true);
            binding.rb3.setVisibility(View.VISIBLE);



            binding.tv1.setTextColor(ContextCompat.getColor(this,R.color.gray4));
            binding.tv2.setTextColor(ContextCompat.getColor(this,R.color.gray4));
            binding.tv3.setTextColor(ContextCompat.getColor(this,R.color.black));


        });

    }

    @Override
    public void back() {
        finish();
    }
}
