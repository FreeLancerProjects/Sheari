package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_client_sign_up.fragments.Fragment_Client_Sign_Up_Step1;
import com.creative.share.apps.sheari.activities_fragments.activity_forget_password.ForgetPasswordActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments.Fragment_Provider_Step3;
import com.creative.share.apps.sheari.databinding.CountryCodeRowBinding;
import com.creative.share.apps.sheari.models.CountryCodeModel;

import java.util.List;

import io.paperdb.Paper;

public class CodeCountryAdapter extends RecyclerView.Adapter<CodeCountryAdapter.MyHolder> {

    private List<CountryCodeModel> list;
    private Context context;
    private String lang;
    private Fragment fragment;
    private AppCompatActivity activity;
    public CodeCountryAdapter(List<CountryCodeModel> list, Context context,Fragment fragment) {

        this.list = list;
        this.context = context;
        this.fragment = fragment;
        this.activity = (AppCompatActivity) context;
        lang = Paper.book().read("lang","ar");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CountryCodeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.country_code_row,parent,false);
        return new CodeCountryAdapter.MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {


        CountryCodeModel model = list.get(position);
        holder.binding.setLang(lang);
        holder.binding.setModel(model);

        holder.itemView.setOnClickListener(view ->
        {
            CountryCodeModel model2 = list.get(holder.getAdapterPosition());

            if (fragment!=null)
            {
                if (fragment instanceof Fragment_Client_Sign_Up_Step1)
                {
                    Fragment_Client_Sign_Up_Step1 fragment_client_sign_up_step1 = (Fragment_Client_Sign_Up_Step1) fragment;
                    fragment_client_sign_up_step1.setItemData(model2);

                }else if (fragment instanceof Fragment_Provider_Step3)
                {
                    Fragment_Provider_Step3 fragment_provider_step3 = (Fragment_Provider_Step3) fragment;
                    fragment_provider_step3.setItemData(model2);
                }
            }else
                {
                    if (activity instanceof ForgetPasswordActivity)
                    {
                        ForgetPasswordActivity forgetPasswordActivity = (ForgetPasswordActivity) activity;
                        forgetPasswordActivity.setItemData(model2);
                    }
                }



        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CountryCodeRowBinding binding;
        public MyHolder(CountryCodeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}

