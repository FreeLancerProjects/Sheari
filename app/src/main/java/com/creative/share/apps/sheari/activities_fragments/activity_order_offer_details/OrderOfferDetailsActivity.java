package com.creative.share.apps.sheari.activities_fragments.activity_order_offer_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.adapters.CommentAdapter;
import com.creative.share.apps.sheari.databinding.ActivityOrderOfferDetailsBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CommentDataModel;
import com.creative.share.apps.sheari.models.CommentRespons;
import com.creative.share.apps.sheari.models.OfferDataModel;
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

public class OrderOfferDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityOrderOfferDetailsBinding binding;
    private String lang;
    private OfferDataModel.Data.Provider provider=null;
    private UserModel userModel;
    private Preferences preferences;
    private CommentAdapter adapter;
    private List<CommentDataModel.Data.Comment> commentList;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_offer_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            provider = (OfferDataModel.Data.Provider) intent.getSerializableExtra("data");
        }
    }


    private void initView() {
        commentList = new ArrayList<>();

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.setModel(provider);

        binding.flAddComment.setOnClickListener(view ->
        {
            userModel = preferences.getUserData(this);
            if (userModel!=null)
            {

                String comment = binding.edtComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment))
                {

                    binding.edtComment.setError(null);
                    Common.CloseKeyBoard(this,binding.edtComment);
                    addComment(comment);

                }else
                    {
                        binding.edtComment.setError(getString(R.string.field_req));
                    }
            }else
                {
                    Intent intent = new Intent(this, SignInActivity.class);
                    intent.putExtra("from",true);
                    startActivity(intent);
                }
        });

        if (userModel!=null)
        {
            getComments();

        }else
            {
                binding.progBar.setVisibility(View.GONE);
            }

    }

    private void getComments() {

        Api.getService(Tags.base_url)
                .getAllComments("Bearer "+userModel.getData().getToken(),provider.getId())
                .enqueue(new Callback<CommentDataModel>() {
                    @Override
                    public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                updateUI(response.body());

                            } else {
                                Toast.makeText(OrderOfferDetailsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(OrderOfferDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OrderOfferDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderOfferDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderOfferDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateUI(CommentDataModel body) {

        commentList.clear();
        commentList.addAll(body.getData().getComments());

        if (adapter==null)
        {
            adapter = new CommentAdapter(commentList,this,body.getData().getUser());
            binding.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.recView.setAdapter(adapter);
        }else
            {
                adapter.notifyDataSetChanged();
            }



    }

    private void addComment(String comment) {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .addComment("Bearer "+userModel.getData().getToken(),provider.getId(),comment)
                .enqueue(new Callback<CommentRespons>() {
                    @Override
                    public void onResponse(Call<CommentRespons> call, Response<CommentRespons> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {

                                getComments();
                                binding.edtComment.setText("");

                            } else {
                                Toast.makeText(OrderOfferDetailsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(OrderOfferDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OrderOfferDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentRespons> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderOfferDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderOfferDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    @Override
    public void back() {
        finish();
    }
}
