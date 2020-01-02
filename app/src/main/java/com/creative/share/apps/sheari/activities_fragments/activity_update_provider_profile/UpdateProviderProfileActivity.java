package com.creative.share.apps.sheari.activities_fragments.activity_update_provider_profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_create_update_project.CreateUpdateProjectActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_payment.PaymentActivity;
import com.creative.share.apps.sheari.adapters.LocationSpinnerAdapter;
import com.creative.share.apps.sheari.adapters.MyFieldAdapter2;
import com.creative.share.apps.sheari.adapters.ProjectAdapter;
import com.creative.share.apps.sheari.databinding.ActivityUpdateProviderProfileBinding;
import com.creative.share.apps.sheari.databinding.DialogSelectImageBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.LocationModel;
import com.creative.share.apps.sheari.models.ProjectDataModel;
import com.creative.share.apps.sheari.models.ProjectModel;
import com.creative.share.apps.sheari.models.UpdateProviderModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProviderProfileActivity extends AppCompatActivity implements Listeners.BackListener {

    private ActivityUpdateProviderProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private UpdateProviderModel updateProviderModel;
    private LinearLayoutManager manager,manager2;
    private List<UserModel.Data.SubCategory> subCategoryList;
    private MyFieldAdapter2 myFieldAdapter;

    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1;
    private Uri imgUri1 = null;
    private int selectedType = 0;
    private List<LocationModel> cityList, countryList,regionList;
    private LocationSpinnerAdapter citySpinnerAdapter, countrySpinnerAdapter,regionSpinnerAdapter;
    private ProjectAdapter projectAdapter;
    private List<ProjectModel> projectModelList;
    private int updated_pos = -1;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_provider_profile);
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        userModel = preferences.getUserData(this);

        if (userModel!=null)
        {
            updateBtnUi();
        }
    }

    private void initView() {
        projectModelList = new ArrayList<>();

        regionList = new ArrayList<>();
        countryList = new ArrayList<>();
        cityList = new ArrayList<>();

        regionList.add(new LocationModel(0, getString(R.string.region)));
        countryList.add(new LocationModel(0, getString(R.string.country2)));
        cityList.add(new LocationModel(0, getString(R.string.city2)));



        subCategoryList = new ArrayList<>();
        updateProviderModel = new UpdateProviderModel();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        countrySpinnerAdapter = new LocationSpinnerAdapter(this,countryList);
        binding.spinnerCountry.setAdapter(countrySpinnerAdapter);

        citySpinnerAdapter = new LocationSpinnerAdapter(this,cityList);
        binding.spinnerCity.setAdapter(citySpinnerAdapter);

        regionSpinnerAdapter = new LocationSpinnerAdapter(this,regionList);
        binding.spinnerRegion.setAdapter(regionSpinnerAdapter);



        if (userModel!=null)
        {
            binding.edtName.setText(userModel.getData().getName());
            binding.edtEmail.setText(userModel.getData().getEmail());
            binding.edtPhone.setText(userModel.getData().getPhone());
            binding.edtAbout.setText(userModel.getData().getBio());

            updateProviderModel.setName(userModel.getData().getName());
            updateProviderModel.setEmail(userModel.getData().getEmail());
            updateProviderModel.setPhone(userModel.getData().getPhone());
            updateProviderModel.setAbout_me(userModel.getData().getBio());


            binding.setModel(updateProviderModel);

            manager2 = new LinearLayoutManager(this);
            binding.recView2.setLayoutManager(manager2);
            projectAdapter = new ProjectAdapter(projectModelList,this,true);
            binding.recView2.setAdapter(projectAdapter);

            getAllProject();
            if (userModel.getData().getSub_categories().size()>0)
            {
                manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                binding.recView.setLayoutManager(manager);
                subCategoryList.addAll(userModel.getData().getSub_categories());
                myFieldAdapter = new MyFieldAdapter2(subCategoryList,this);
                binding.recView.setAdapter(myFieldAdapter);
                binding.tvFields.setVisibility(View.GONE);
            }else
                {
                    binding.tvFields.setVisibility(View.VISIBLE);

                }


            Picasso.with(this).load(Uri.parse(userModel.getData().getImage())).fit().placeholder(R.drawable.user_avatar).into(binding.image);

            updateBtnUi();
        }


        try {
            binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        cityList.clear();
                        cityList.add(new LocationModel(0, getString(R.string.city2)));
                        citySpinnerAdapter.notifyDataSetChanged();

                    }else
                    {
                        getCity(countryList.get(i).getId());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {

                        regionList.clear();
                        regionList.add(new LocationModel(0, getString(R.string.region)));
                        regionSpinnerAdapter.notifyDataSetChanged();

                    }else
                    {
                        int city_id = cityList.get(i).getId();

                        getRegion(city_id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });




            binding.spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i==0)
                    {
                        updateProviderModel.setRegion_id(0);

                    }else
                    {
                        int region_id = regionList.get(i).getId();
                        updateProviderModel.setRegion_id(region_id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }catch (Exception e){}

        binding.btnUpdate.setOnClickListener(view -> {
            if (updateProviderModel.isValid(this))
            {
                update();
            }
        });
        binding.flSelectImage.setOnClickListener(view -> CreateImageAlertDialog(IMG_REQ1));
        binding.btnDiscount.setOnClickListener(view ->
        {
            userModel = preferences.getUserData(this);

            if (userModel.getData().getIs_special().equals("0"))
            {
                Intent intent = new Intent(this, PaymentActivity.class);
                startActivity(intent);
            }else if (userModel.getData().getIs_special().equals("1"))
            {

                String discount = binding.edtDiscount.getText().toString().trim();
                if (discount.isEmpty())
                {
                    binding.edtDiscount.setError(getString(R.string.field_req));

                }else
                    {
                        binding.edtDiscount.setError(null);
                        Common.CloseKeyBoard(this,binding.edtDiscount);
                        update();

                    }
            }
        });

        binding.btnAddPro.setOnClickListener(view ->
        {
            Intent intent = new Intent(this, CreateUpdateProjectActivity.class);
            startActivityForResult(intent,100);
        });
        getCountry();
    }

    private void getAllProject() {

        Api.getService(Tags.base_url)
                .getAllProjects("Bearer "+userModel.getData().getToken())
                .enqueue(new Callback<ProjectDataModel>() {
                    @Override
                    public void onResponse(Call<ProjectDataModel> call, Response<ProjectDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                projectModelList.clear();
                                projectModelList.addAll(response.body().getData());
                                if (projectModelList.size()>0)
                                {
                                    binding.tvNoWorks.setVisibility(View.GONE);
                                    projectAdapter.notifyDataSetChanged();

                                }else
                                    {
                                        binding.tvNoWorks.setVisibility(View.VISIBLE);

                                    }


                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(UpdateProviderProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(UpdateProviderProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProviderProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void updateBtnUi() {

        if (userModel.getData().getIs_special().equals("0"))
        {
            binding.btnDiscount.setText(getString(R.string.upgrade));
        }else if (userModel.getData().getIs_special().equals("1"))
        {
            binding.btnDiscount.setText(getString(R.string.update));

        }
    }

    private void getCountry() {

        Api.getService(Tags.base_url)
                .getCountry(lang)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                countryList.clear();
                                countryList.add(new LocationModel(0, getString(R.string.country2)));
                                countryList.addAll(response.body().getData());
                                runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());

                                updateCountrySpinner();
                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(UpdateProviderProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDataModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(UpdateProviderProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProviderProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getCity(int country_id) {

        Api.getService(Tags.base_url)
                .getCityByCountry(lang,country_id)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                cityList.clear();
                                cityList.add(new LocationModel(0, getString(R.string.city2)));
                                cityList.addAll(response.body().getData());
                                runOnUiThread(()->citySpinnerAdapter.notifyDataSetChanged());

                                updateCitySpinner();

                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(UpdateProviderProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDataModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(UpdateProviderProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProviderProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getRegion(int city_id)
    {
        Api.getService(Tags.base_url)
                .getRegionByCity(lang,city_id)
                .enqueue(new Callback<LocationDataModel>() {
                    @Override
                    public void onResponse(Call<LocationDataModel> call, Response<LocationDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isValue()) {
                                try {
                                    regionList.clear();
                                    regionList.add(new LocationModel(0, getString(R.string.region)));
                                    regionList.addAll(response.body().getData());
                                    runOnUiThread(()->regionSpinnerAdapter.notifyDataSetChanged());
                                    updateRegionSpinner();
                                }catch (Exception e){}


                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(UpdateProviderProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UpdateProviderProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDataModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(UpdateProviderProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProviderProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateCountrySpinner()
    {
        for (int i =0;i<countryList.size()-1;i++)
        {
            if (userModel.getData().getCountry_id()==countryList.get(i).getId())
            {
                binding.spinnerCountry.setSelection(i);
                getCity(userModel.getData().getCountry_id());
                return;
            }
        }
    }

    private void updateCitySpinner()
    {
        for (int i =0;i<cityList.size()-1;i++)
        {
            if (userModel.getData().getCity_id()==cityList.get(i).getId())
            {
                binding.spinnerCity.setSelection(i);
                getRegion(userModel.getData().getCity_id());
                return;
            }
        }
    }

    private void updateRegionSpinner()
    {
        for (int i =0;i<regionList.size()-1;i++)
        {
            if (userModel.getData().getRegion_id()==regionList.get(i).getId())
            {
                binding.spinnerRegion.setSelection(i);
                return;
            }
        }
    }

    private void update()
    {



    }


    private void CreateImageAlertDialog(final int img_req)
    {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();


        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.dialog_select_image,null,false);




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
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1,img_req);
        }
    }

    private void Check_CameraPermission(int img_req)
    {
        if (ContextCompat.checkSelfPermission(this,camera_permission)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,write_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{camera_permission,write_permission},img_req);
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
                Toast.makeText(this,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null)
        {
            if (selectedType == 1)
            {
                imgUri1 = data.getData();
                updateProviderModel.setImage(imgUri1);
                File file = new File(Common.getImagePath(this, imgUri1));
                Picasso.with(this).load(file).fit().into(binding.image);
            }else if (selectedType ==2)
            {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                updateProviderModel.setImage(imgUri1);

                if (imgUri1 != null) {
                    String path = Common.getImagePath(this, imgUri1);

                    if (path != null) {
                        Picasso.with(this).load(new File(path)).fit().into(binding.image);

                    } else {
                        Picasso.with(this).load(imgUri1).fit().into(binding.image);

                    }
                }
            }




        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null)
        {

            ProjectModel projectModel = (ProjectModel) data.getSerializableExtra("data");
            projectModelList.add(projectModel);
            projectAdapter.notifyItemInserted(projectModelList.size()-1);

        }else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null)
        {
            if (updated_pos!=-1)
            {
                ProjectModel projectModel = (ProjectModel) data.getSerializableExtra("data");
                projectModelList.set(updated_pos,projectModel);
                projectAdapter.notifyItemChanged(updated_pos);
            }

            updated_pos = -1;
        }

    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }


    public void setItem(ProjectModel model, int adapterPosition) {
        updated_pos = adapterPosition;
        Intent intent = new Intent(this, CreateUpdateProjectActivity.class);
        intent.putExtra("data",model);
        startActivityForResult(intent,200);
    }

    @Override
    public void back() {
        finish();
    }


}
