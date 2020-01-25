package com.creative.share.apps.sheari.activities_fragments.activity_order_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_chat.ChatActivity;
import com.creative.share.apps.sheari.databinding.ActivityOrderDetailsBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ChatUserModel;
import com.creative.share.apps.sheari.models.MyOrderDataModel;
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

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityOrderDetailsBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private MyOrderDataModel.Data.OrderModel orderModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            orderModel = (MyOrderDataModel.Data.OrderModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUser(userModel);
        binding.setModel(orderModel);


        binding.imageChat.setOnClickListener(view -> {

            String image ="";
            if (userModel.getData().getRole().equals("provider"))
            {
                image = orderModel.getClient_image();
            }else
            {
                image = orderModel.getProvider_image();
            }

            int chat_id ;


            if (userModel.getData().getRole().equals("provider"))
            {
                chat_id = orderModel.getUser_id();
            }else
            {
                chat_id = orderModel.getProvider_id();
            }


            ChatUserModel chatUserModel = new ChatUserModel(orderModel.getName(),image,chat_id,"",orderModel.getId());

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("data",chatUserModel);
            startActivity(intent);

        });

        binding.btnAccept.setOnClickListener(view -> accept());

        binding.btnRefuse.setOnClickListener(view -> refuse());

        binding.btnDone.setOnClickListener(view -> finishOrder());

    }

    private void accept() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .acceptOrder(lang,"Bearer "+userModel.getData().getToken(),orderModel.getId())
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                                    Intent intent = getIntent();
                                    setResult(RESULT_OK,intent);
                                    finish();

                                }else
                                {
                                    Toast.makeText(OrderDetailsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(OrderDetailsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(OrderDetailsActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void refuse() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .refuseOrder(lang,"Bearer "+userModel.getData().getToken(),orderModel.getId(),"لا يوجد سبب")
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                                    Intent intent = getIntent();
                                    setResult(RESULT_OK,intent);
                                    finish();

                                }else
                                {
                                    Toast.makeText(OrderDetailsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(OrderDetailsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(OrderDetailsActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void finishOrder() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        try {

            Api.getService(Tags.base_url)
                    .finishOrder(lang,"Bearer "+userModel.getData().getToken(),orderModel.getId())
                    .enqueue(new Callback<ResponseActiveUser>() {
                        @Override
                        public void onResponse(Call<ResponseActiveUser> call, Response<ResponseActiveUser> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                                    Intent intent = getIntent();
                                    setResult(RESULT_OK,intent);
                                    finish();

                                }else
                                {
                                    Toast.makeText(OrderDetailsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(OrderDetailsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(OrderDetailsActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
