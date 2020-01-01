package com.creative.share.apps.sheari.activities_fragments.activity_update_client_profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ActivityUpdateClientProfileBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UpdateClientModel;
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

public class UpdateClientProfileActivity extends AppCompatActivity implements Listeners.BackListener {

    private ActivityUpdateClientProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private UpdateClientModel updateClientModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_client_profile);
        initView();
    }

    private void initView() {
        updateClientModel = new UpdateClientModel();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        if (userModel!=null)
        {
            binding.edtName.setText(userModel.getData().getName());
            binding.edtEmail.setText(userModel.getData().getEmail());
            binding.edtPhone.setText(userModel.getData().getPhone());

            updateClientModel.setName(userModel.getData().getName());
            updateClientModel.setEmail(userModel.getData().getEmail());
            updateClientModel.setPhone(userModel.getData().getPhone());

            binding.setModel(updateClientModel);
        }

        binding.btnUpdate.setOnClickListener(view -> {
            if (updateClientModel.isValid(this))
            {
                update();
            }
        });
    }


    private void update()
    {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .updateClientProfile("Bearer "+userModel.getData().getToken(),updateClientModel.getName(),updateClientModel.getEmail(),updateClientModel.getPhone())
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    preferences.create_update_userData(UpdateClientProfileActivity.this,response.body());
                                    Intent intent = getIntent();
                                    setResult(RESULT_OK,intent);
                                    finish();


                                }else
                                {
                                    Toast.makeText(UpdateClientProfileActivity.this,response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(UpdateClientProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(UpdateClientProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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
                                        Toast.makeText(UpdateClientProfileActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(UpdateClientProfileActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void back() {
        finish();
    }
}
