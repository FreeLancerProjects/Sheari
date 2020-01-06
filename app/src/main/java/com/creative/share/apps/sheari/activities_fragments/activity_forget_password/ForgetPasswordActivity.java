package com.creative.share.apps.sheari.activities_fragments.activity_forget_password;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_verification_code_forgot_password.ForgotPasswordCodeActivity;
import com.creative.share.apps.sheari.adapters.CodeCountryAdapter;
import com.creative.share.apps.sheari.databinding.ActivityForgetPasswordBinding;
import com.creative.share.apps.sheari.databinding.DialogCountryCodeBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CountryCodeModel;
import com.creative.share.apps.sheari.models.ResponseActiveUser;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityForgetPasswordBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private CodeCountryAdapter codeCountryAdapter;
    private List<CountryCodeModel> countryCodeModelList;
    private AlertDialog dialog;
    private String code="966";

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);
        initView();
    }

    private void initView() {
        countryCodeModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


        countryCodeModelList.add(new CountryCodeModel("المملكة العربية السعودية","Saudi Arabia","966",R.drawable.flag_sa));
        countryCodeModelList.add(new CountryCodeModel("الكويت","Kuwait","965",R.drawable.flag_kw));
        countryCodeModelList.add(new CountryCodeModel("البحرين","Bahrain","973",R.drawable.flag_bh));
        countryCodeModelList.add(new CountryCodeModel("عمان","Oman","968",R.drawable.flag_om));
        countryCodeModelList.add(new CountryCodeModel("الإمارات","United Arab Emirates","971",R.drawable.flag_ae));
        createCountryCodeDialog();


        binding.iconCode.setOnClickListener(view -> dialog.show());
        binding.btnSend.setOnClickListener(view -> checkData());


    }

    private void checkData() {

        String phone = binding.edtPhone.getText().toString().trim();

        if (!phone.isEmpty())
        {
            Common.CloseKeyBoard(this,binding.edtPhone);
            binding.edtPhone.setError(null);
            resetPassword(phone);
        }else
            {
                binding.edtPhone.setError(getString(R.string.field_req));
            }
    }

    private void resetPassword(String phone) {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .forgotPassword(code+phone)
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {


                                if (response.body().isValue())
                                {
                                    String p = code+phone;
                                    Intent intent = new Intent(ForgetPasswordActivity.this, ForgotPasswordCodeActivity.class);
                                    intent.putExtra("phone",p);
                                    startActivity(intent);

                                }else
                                {
                                    Toast.makeText(ForgetPasswordActivity.this,response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(ForgetPasswordActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(ForgetPasswordActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseActiveUser> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(ForgetPasswordActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(ForgetPasswordActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }
    }

    private void createCountryCodeDialog() {
        dialog = new AlertDialog.Builder(this)
                .create();

        DialogCountryCodeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_country_code, null, false);

        codeCountryAdapter = new CodeCountryAdapter(countryCodeModelList,this,null);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(codeCountryAdapter);


        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
    }


    public void setItemData(CountryCodeModel model) {
        code = model.getCode();
        binding.tvCode.setText("+"+model.getCode());
        dialog.dismiss();
    }

    @Override
    public void back() {
        finish();
    }
}
