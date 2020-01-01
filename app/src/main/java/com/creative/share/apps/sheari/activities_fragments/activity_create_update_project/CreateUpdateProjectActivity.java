package com.creative.share.apps.sheari.activities_fragments.activity_create_update_project;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ActivityCreateUpdateProjectBinding;
import com.creative.share.apps.sheari.databinding.DialogSelectImageBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.CreateProjectModel;
import com.creative.share.apps.sheari.models.ProjectModel;
import com.creative.share.apps.sheari.models.SingleProjectDataModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.share.Common;
import com.creative.share.apps.sheari.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUpdateProjectActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityCreateUpdateProjectBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private ProjectModel projectModel=null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1;
    private Uri imgUri1 = null;
    private int selectedType = 0;
    private CreateProjectModel createProjectModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_update_project);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("data"))
            {
                projectModel = (ProjectModel) intent.getSerializableExtra("data");
            }
        }
    }

    private void initView() {
        createProjectModel = new CreateProjectModel();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(createProjectModel);

        if (projectModel!=null)
        {
            binding.icon.setVisibility(View.GONE);
            Picasso.with(this).load(Uri.parse(projectModel.getFile())).fit().into(binding.image);
            binding.btnUpdate.setText(getString(R.string.update));

            createProjectModel.setTitle(projectModel.getTitle());
            createProjectModel.setDescription(projectModel.getDescription());
            binding.setModel(createProjectModel);

        }else
            {
                binding.icon.setVisibility(View.VISIBLE);
                binding.btnUpdate.setText(R.string.create_pro);
            }


        binding.flSelectImage.setOnClickListener(view -> CreateImageAlertDialog(IMG_REQ1));
        binding.btnUpdate.setOnClickListener(view ->
        {

            if (projectModel!=null)
            {
                if (createProjectModel.isValidUpdate(this))
                {
                    if (imgUri1==null)
                    {
                        updateProjectWithImage();

                    }else
                    {
                        updateProject();
                    }
                }

            }else
                {
                    if (createProjectModel.isValidCreate(this))
                    {
                        createProject();

                    }
                }

        });

    }

    private void createProject() {

        Log.e("ddd","ffff");
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();


        RequestBody title_part= Common.getRequestBodyText(createProjectModel.getTitle());
        RequestBody description_part= Common.getRequestBodyText(createProjectModel.getDescription());
        MultipartBody.Part image = Common.getMultiPart(this,imgUri1,"file");


        try {

            Api.getService(Tags.base_url)
                    .addProject("Bearer "+userModel.getData().getToken(),title_part,description_part,image)
                    .enqueue(new Callback<SingleProjectDataModel>() {
                        @Override
                        public void onResponse(Call<SingleProjectDataModel> call, Response<SingleProjectDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().isValue())
                                {
                                    Intent intent = getIntent();
                                    intent.putExtra("data",response.body().getData());
                                    setResult(RESULT_OK,intent);
                                    finish();

                                }else
                                {
                                    Toast.makeText(CreateUpdateProjectActivity.this,response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {

                                if (response.code() == 500) {
                                    Toast.makeText(CreateUpdateProjectActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(CreateUpdateProjectActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleProjectDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(CreateUpdateProjectActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(CreateUpdateProjectActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }

    }


    private void updateProject() {


    }
    private void updateProjectWithImage() {

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
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1)
            {
                binding.icon.setVisibility(View.GONE);
                imgUri1 = data.getData();
                createProjectModel.setImage(imgUri1);
                File file = new File(Common.getImagePath(this, imgUri1));
                Picasso.with(this).load(file).fit().into(binding.image);
            }else if (selectedType ==2)
            {
                binding.icon.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                createProjectModel.setImage(imgUri1);

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





    @Override
    public void back() {
        finish();
    }
}
