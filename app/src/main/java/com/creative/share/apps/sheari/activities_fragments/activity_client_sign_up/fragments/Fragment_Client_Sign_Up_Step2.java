package com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.ClientSignUpActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_terms.TermsActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_verify_code.VerifyCodeActivity;
import com.creative.share.apps.sheari.databinding.FragmentClientSignUpStep2Binding;
import com.creative.share.apps.sheari.models.ClientSignUpModel;
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

public class Fragment_Client_Sign_Up_Step2 extends Fragment {
    private static final String TAG = "DATA";
    private static final String TAG2 = "OUT";

    private FragmentClientSignUpStep2Binding binding;
    private ClientSignUpActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private ClientSignUpModel clientSignUpModel;
    private boolean out = false;



    public static Fragment_Client_Sign_Up_Step2 newInstance(ClientSignUpModel clientSignUpModel, boolean out)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,  clientSignUpModel);
        bundle.putBoolean(TAG2,out);

        Fragment_Client_Sign_Up_Step2 fragment_client_sign_up_step2 =new Fragment_Client_Sign_Up_Step2();
        fragment_client_sign_up_step2.setArguments(bundle);

        return fragment_client_sign_up_step2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_sign_up_step2, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        activity = (ClientSignUpActivity) getActivity();

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            clientSignUpModel = (ClientSignUpModel) bundle.getSerializable(TAG);
            binding.setClientSignUp(clientSignUpModel);
            out = bundle.getBoolean(TAG2);



        }

        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.btnDone.setOnClickListener((view -> {

            if (clientSignUpModel.step2IsValid(activity))
            {
                signUp();

            }
        }));
        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                clientSignUpModel.setAcceptTerms(true);
                Intent intent = new Intent(activity, TermsActivity.class);
                startActivity(intent);
            }else
                {
                    clientSignUpModel.setAcceptTerms(false);

                }



        });



    }

    private void updateProfile() {


    }

    private void signUp() {

        Log.e("name",clientSignUpModel.getName()+"__");
        Log.e("email",clientSignUpModel.getEmail()+"__");
        Log.e("phone",clientSignUpModel.getPhone_code()+clientSignUpModel.getPhone()+"__");
        Log.e("password",clientSignUpModel.getPassword()+"__");
        Log.e("region",clientSignUpModel.getRegion_id()+"__");

        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .clientSignUp(lang,clientSignUpModel.getName(),clientSignUpModel.getEmail(),clientSignUpModel.getPhone_code()+clientSignUpModel.getPhone(),clientSignUpModel.getRegion_id(),clientSignUpModel.getPassword())
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isStatus())
                                {
                                    if (response.body().getUser().getIs_verified().equals("0"))
                                    {
                                        Intent intent = new Intent(activity, VerifyCodeActivity.class);
                                        intent.putExtra("data",response.body());
                                        intent.putExtra("out",out);
                                        startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        preferences.create_update_userData(activity,response.body());
                                        preferences.createSession(activity, Tags.session_login);

                                        if (!out)
                                        {
                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            startActivity(intent);

                                        }else
                                        {
                                            Intent intent = activity.getIntent();
                                            activity.setResult(Activity.RESULT_OK,intent);
                                        }
                                        activity.finish();
                                    }


                                }else
                                {
                                    if (!response.body().getMessage().isEmpty()&&response.body().getMessage()!=null)
                                    {
                                        Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                    }else if (response.body().getMsg().isEmpty()&&response.body().getMsg()!=null)
                                    {
                                        Toast.makeText(activity,response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                    }
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }
    }


}
