package com.creative.share.apps.sheari.activities_fragments.activity_verify_code;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.databinding.ActivityVerifyCodeBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ResponseActiveUser;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityVerifyCodeBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private boolean canResend = true;
    private CountDownTimer countDownTimer;
    private boolean out = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify_code);
        getDataFromIntent();
        initView();
    }



    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.btnConfirm.setOnClickListener(v -> checkData());

        binding.btnResend.setOnClickListener(v -> {

            if (canResend)
            {
                reSendSMSCode();
            }
        });



        startCounter();

    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data")&&intent.hasExtra("out"))
        {
            userModel = (UserModel) intent.getSerializableExtra("data");
            out = intent.getBooleanExtra("out",false);
        }
    }


    private void checkData() {
        String code = binding.edtCode.getText().toString().trim();
        if (!TextUtils.isEmpty(code))
        {
            Common.CloseKeyBoard(this,binding.edtCode);
            ValidateCode(code);
        }else
        {
            binding.edtCode.setError(getString(R.string.field_req));
        }
    }


    private void ValidateCode(String code)
    {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .activeUserSmsCode("Bearer "+userModel.getData().getToken(),code,userModel.getData().getPhone())
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    userModel.getData().setIs_verified("1");
                                    preferences.create_update_userData(VerifyCodeActivity.this,userModel);
                                    preferences.createSession(VerifyCodeActivity.this, Tags.session_login);

                                    if (!out)
                                    {
                                        Intent intent = new Intent(VerifyCodeActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }

                                    finish();
                                }else
                                    {
                                        Toast.makeText(VerifyCodeActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(VerifyCodeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(VerifyCodeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(VerifyCodeActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void reSendSMSCode() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .reSendSmsCode(userModel.getData().getPhone())
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    startCounter();

                                }else
                                {
                                    Toast.makeText(VerifyCodeActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(VerifyCodeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(VerifyCodeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(VerifyCodeActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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

        /*final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .resendCode(userModel.getId())
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                            dialog.dismiss();

                            if (response.isSuccessful())
                            {
                                startCounter();

                            }else
                            {
                                try {
                                    Log.e("error_code",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code()==422)
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else if (response.code()==500)
                                {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e)
                            {
                                Log.e("Exe",e.getMessage()+"__");
                                dialog.dismiss();
                            }
                        }
                    });
        }catch (Exception e)
        {
            dialog.dismiss();
            Log.e("ddd",e.getMessage()+"__");
        }*/

    }

    private void startCounter()
    {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds= AllSeconds%60;
                binding.btnResend.setText("00:"+seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setText(getString(R.string.resend));
            }
        }.start();
    }



    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
    }
}
