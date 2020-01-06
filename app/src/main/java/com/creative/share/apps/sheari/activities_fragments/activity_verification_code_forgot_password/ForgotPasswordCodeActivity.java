package com.creative.share.apps.sheari.activities_fragments.activity_verification_code_forgot_password;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ActivityForgotPasswordCodeBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ResponseActiveUser;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordCodeActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityForgotPasswordCodeBinding binding;
    private String lang;
    private String phone;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password_code);
        getDataFromIntent();
        initView();
    }



    private void initView() {

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setPhone(phone);
        binding.btnConfirm.setOnClickListener(v -> checkData());


    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("phone"))
        {
            phone = intent.getStringExtra("phone");
        }
    }


    private void checkData() {
        String code = binding.edtCode.getText().toString().trim();
        String pass = binding.edtPassword.getText().toString().trim();
        String re_pass = binding.edtRePassword.getText().toString().trim();

        if (!TextUtils.isEmpty(code)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(re_pass)&&pass.equals(re_pass))
        {
            binding.edtCode.setError(null);
            binding.edtPassword.setError(null);
            binding.edtRePassword.setError(null);

            Common.CloseKeyBoard(this,binding.edtCode);
            restPassword(code,pass);
        }else
        {
            if (TextUtils.isEmpty(code))
            {
                binding.edtCode.setError(getString(R.string.field_req));

            }else
                {
                    binding.edtCode.setError(null);

                }

            if (pass.isEmpty()) {
                binding.edtPassword.setError(getString(R.string.field_req));
            } else if (pass.length() < 6) {
                binding.edtPassword.setError(getString(R.string.pass_short));
            } else if (!pass.equals(re_pass)) {
                binding.edtRePassword.setError(getString(R.string.re_pass_not_match));
            } else {
                binding.edtPassword.setError(null);
                binding.edtRePassword.setError(null);
            }

        }
    }

    private void restPassword(String code, String pass)
    {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .sendNewPassword(code,phone,pass,pass)
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                   finish();

                                }else
                                {
                                    if (response.body().getMsg()!=null&&!response.body().getMsg().isEmpty())
                                    {
                                        Toast.makeText(ForgotPasswordCodeActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                    }else if (response.body().getMessage()!=null&&!response.body().getMessage().isEmpty())
                                    {
                                        Toast.makeText(ForgotPasswordCodeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(ForgotPasswordCodeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(ForgotPasswordCodeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(ForgotPasswordCodeActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e)
                            {
                                Log.e("rrr",e.getMessage()+"_");

                            }
                        }
                    });
        }catch (Exception e)
        {
            dialog.dismiss();
            Log.e("dddd",e.getMessage()+"_");
        }
    }




    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
