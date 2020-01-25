package com.creative.share.apps.sheari.activities_fragments.activity_payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.paypal_activity.PayPalActivity;
import com.creative.share.apps.sheari.databinding.ActivityPaymentBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.PayPalLinkModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPaymentActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityPaymentBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;



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
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);





        binding.card1.setOnClickListener(view -> {

            pay();
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

    private void pay() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .getPayPalLink("Bearer "+userModel.getData().getToken(),"")
                    .enqueue(new Callback<PayPalLinkModel>() {
                        @Override
                        public void onResponse(Call<PayPalLinkModel> call, Response<PayPalLinkModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    Log.e("body",response.body().getData()+"______");
                                    Intent intent = new Intent(MyPaymentActivity.this, PayPalActivity.class);
                                    intent.putExtra("data",response.body());
                                    startActivityForResult(intent,100);



                                }else
                                {
                                    Toast.makeText(MyPaymentActivity.this,R.string.failed, Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(MyPaymentActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(MyPaymentActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PayPalLinkModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(MyPaymentActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(MyPaymentActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK)
        {

            userModel.getData().setIs_special("1");
            preferences.create_update_userData(this,userModel);
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            finish();
        }

    }

    @Override
    public void back() {
        finish();
    }
}
