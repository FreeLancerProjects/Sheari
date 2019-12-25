package com.creative.share.apps.sheari.activities_fragments.activity_sign_in.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_forget_password.ForgetPasswordActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.sheari.databinding.FragmentSignInBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.LoginModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Sign_In extends Fragment implements Listeners.LoginListener {
    private FragmentSignInBinding binding;
    private SignInActivity activity;
    private String lang;
    private LoginModel loginModel;
    private Preferences preferences;

    public static Fragment_Sign_In newInstance() {
        return new Fragment_Sign_In();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        loginModel = new LoginModel();
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setLoginModel(loginModel);
        binding.setLoginListener(this);
        binding.btnSignUp.setOnClickListener((view -> activity.back()));
        binding.btnForgetPassword.setOnClickListener((view ->
        {
            Intent intent = new Intent(activity, ForgetPasswordActivity.class);
            startActivity(intent);
        }
        ));

        binding.tvSkip.setOnClickListener((view ->
        {
            Intent intent = new Intent(activity, HomeActivity.class);
            startActivity(intent);
            activity.finish();
        }
        ));


    }


    @Override
    public void checkDataLogin() {

        if (loginModel.isDataValid(activity)) {
            Common.CloseKeyBoard(activity, binding.edtEmail);
            login(loginModel);
        }
    }

    private void login(LoginModel loginModel) {





        /*ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .login(loginModel.getPhone_code(),loginModel.getPhone(),1)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                preferences.create_update_userData(activity,response.body());
                                preferences.createSession(activity, Tags.session_login);

                                if (!activity.isOut)
                                {
                                    Intent intent = new Intent(activity, HomeActivity.class);
                                    startActivity(intent);
                                }


                                activity.finish();

                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                }else if (response.code()==401)
                                {
                                    try {
                                        UserModel userModel = new Gson().fromJson(response.errorBody().string(),UserModel.class);
                                        CreateDialogAlert(userModel);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }else if (response.code()==402)
                                {
                                    Toast.makeText(activity, R.string.blokced, Toast.LENGTH_SHORT).show();

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

        }*/
    }


}
