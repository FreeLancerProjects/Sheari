package com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_sign_up.ProviderSignUpActivity;
import com.creative.share.apps.sheari.adapters.CodeCountryAdapter;
import com.creative.share.apps.sheari.databinding.DialogCountryCodeBinding;
import com.creative.share.apps.sheari.databinding.DialogSelectImageBinding;
import com.creative.share.apps.sheari.databinding.FragmentProviderStep3Binding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.models.CountryCodeModel;
import com.creative.share.apps.sheari.models.ProviderSignUpModel;
import com.creative.share.apps.sheari.share.Common;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Provider_Step3 extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener {

    private FragmentProviderStep3Binding binding;
    private ProviderSignUpActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private Listeners.ProviderSteps listener = null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1;
    private Uri imgUri1 = null;
    private int selectedType = 0;
    private ProviderSignUpModel providerSignUpModel;
    private CodeCountryAdapter codeCountryAdapter;
    private List<CountryCodeModel> countryCodeModelList;
    private AlertDialog dialog;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (listener==null)
        {
            listener = (Listeners.ProviderSteps) context;
        }
    }




    public static Fragment_Provider_Step3 newInstance() {
        return new Fragment_Provider_Step3();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_step3, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        countryCodeModelList = new ArrayList<>();

        activity = (ProviderSignUpActivity) getActivity();
        providerSignUpModel = activity.getProviderSignUpModel();
        binding.tvCode.setText("+966");
        providerSignUpModel.setPhone_code("966");
        binding.setSignUpModel(providerSignUpModel);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setShowCountryDialogListener(this);


        countryCodeModelList.add(new CountryCodeModel("المملكة العربية السعودية","Saudi Arabia","966",R.drawable.flag_sa));
        countryCodeModelList.add(new CountryCodeModel("الكويت","Kuwait","965",R.drawable.flag_kw));
        countryCodeModelList.add(new CountryCodeModel("البحرين","Bahrain","973",R.drawable.flag_bh));
        countryCodeModelList.add(new CountryCodeModel("عمان","Oman","968",R.drawable.flag_om));
        countryCodeModelList.add(new CountryCodeModel("الإمارات","United Arab Emirates","971",R.drawable.flag_ae));
        createCountryCodeDialog();


        binding.btnNext.setOnClickListener((view ->{
            if (providerSignUpModel.step3IsValid(activity))
            {
                listener.step4();
                activity.setProviderSignUpModel(providerSignUpModel);
            }

        } ));
        binding.btnPrevious.setOnClickListener((view -> activity.back()));
        binding.btnSelectImage.setOnClickListener((view -> CreateImageAlertDialog(IMG_REQ1)));

        //createCountryDialog();

    }

    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM() != null) {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            } else {
                String code = "+966";
                binding.tvCode.setText(code);
                providerSignUpModel.setPhone_code(code.replace("+", "00"));
            }
        } catch (Exception e) {
            String code = "+966";
            binding.tvCode.setText(code);
            providerSignUpModel.setPhone_code(code.replace("+", "00"));
        }


    }

    private void updatePhoneCode(Country country) {

        binding.tvCode.setText(country.getDialCode());
        providerSignUpModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    @Override
    public void showDialog() {
        //countryPicker.showDialog(activity);
        dialog.show();
    }

    private void createCountryCodeDialog() {
        dialog = new AlertDialog.Builder(activity)
                .create();

        DialogCountryCodeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_country_code, null, false);

        codeCountryAdapter = new CodeCountryAdapter(countryCodeModelList,activity,this);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recView.setAdapter(codeCountryAdapter);


        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
    }


    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }



    private void CreateImageAlertDialog(final int img_req)
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.dialog_select_image,null,false);




        binding.btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            selectedType = 2;
            Check_CameraPermission(img_req);

        });

        binding.btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            selectedType = 1;
            CheckReadPermission(img_req);



        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }
    private void CheckReadPermission(int img_req)
    {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1,img_req);
        }
    }

    private void Check_CameraPermission(int img_req)
    {
        if (ContextCompat.checkSelfPermission(activity,camera_permission)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(activity,write_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{camera_permission,write_permission},img_req);
        }else
        {
            SelectImage(2,img_req);

        }

    }
    private void SelectImage(int type,int img_req) {

        Intent intent = new Intent();

        if (type == 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,img_req);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,img_req);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {

            if (selectedType ==1)
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1)
            {
                imgUri1 = data.getData();
                providerSignUpModel.setImage_uri(imgUri1);
                File file = new File(Common.getImagePath(activity, imgUri1));
                Picasso.with(activity).load(file).fit().into(binding.image);
            }else if (selectedType ==2)
            {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                providerSignUpModel.setImage_uri(imgUri1);

                if (imgUri1 != null) {
                    String path = Common.getImagePath(activity, imgUri1);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(binding.image);

                    } else {
                        Picasso.with(activity).load(imgUri1).fit().into(binding.image);

                    }
                }
            }




        }

    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }


    public void setItemData(CountryCodeModel model) {
        binding.tvCode.setText("+"+model.getCode());
        providerSignUpModel.setPhone_code(model.getCode());
        dialog.dismiss();
    }
}
