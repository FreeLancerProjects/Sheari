package com.creative.share.apps.sheari.activities_fragments.activity_home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_ads.AdsActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_home.fragments.Fragment_Event_Design;
import com.creative.share.apps.sheari.activities_fragments.activity_home.fragments.Fragment_Home;
import com.creative.share.apps.sheari.activities_fragments.activity_home.fragments.Fragment_Market;
import com.creative.share.apps.sheari.activities_fragments.activity_home.fragments.Fragment_Search;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.MyOrderActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_payment.PaymentActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_profile.ProfileActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_provider_profile.ProviderProfileActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_terms.TermsActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_update_client_profile.UpdateClientProfileActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_update_provider_profile.UpdateProviderProfileActivity;
import com.creative.share.apps.sheari.adapters.ViewPagerAdapter;
import com.creative.share.apps.sheari.databinding.DialogLanguageBinding;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TabLayout tab;
    private ViewPager pager;
    private Preferences preferences;
    private UserModel userModel;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titles;
    private FragmentManager manager;
    private String lang;
    private CircleImageView image;
    private TextView tvName;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();


    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        manager = getSupportFragmentManager();
        fragmentList = new ArrayList<>();
        titles = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
        image = view.findViewById(R.id.image);
        tvName = view.findViewById(R.id.tvName);

        toolbar = findViewById(R.id.toolbar);

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        addFragments_Titles();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(fragmentList);
        pager.setOffscreenPageLimit(fragmentList.size());
        adapter.addTitles(titles);
        pager.setAdapter(adapter);

        updateUi();


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateMenu();
    }

    private void updateMenu() {

        if (userModel != null) {


            if (userModel.getData() != null && userModel.getData().getRole().equals("client")) {
                navigationView.getMenu().getItem(1).setVisible(false);
                navigationView.getMenu().getItem(5).setVisible(false);
            } else if (userModel.getData() != null && userModel.getData().getRole().equals("provider")) {
                navigationView.getMenu().getItem(2).setVisible(false);

            }


        }
    }

    private void updateUi() {


        if (userModel != null) {

            /*if (userModel.getData() != null && userModel.getData().getRole().equals("client")) {
                navigationView.getMenu().getItem(1).setVisible(false);
                navigationView.getMenu().getItem(5).setVisible(false);
            } else if (userModel.getData() != null && userModel.getData().getRole().equals("provider")) {
                navigationView.getMenu().getItem(3).setVisible(false);

            }*/


            if (userModel.getData() != null && userModel.getData().getImage() != null) {
                Picasso.with(this).load(Uri.parse(userModel.getData().getImage())).placeholder(R.drawable.user_avatar).fit().into(image);

            }

            if (userModel.getData() != null && userModel.getData().getName() != null) {
                tvName.setText(userModel.getData().getName());
            }

        }


    }

    private void addFragments_Titles() {
        fragmentList.add(Fragment_Home.newInstance());
        fragmentList.add(Fragment_Event_Design.newInstance());
        fragmentList.add(Fragment_Market.newInstance());
        fragmentList.add(Fragment_Search.newInstance());

        titles.add(getString(R.string.depts));
        titles.add(getString(R.string.events_des));
        titles.add(getString(R.string.market));
        titles.add(getString(R.string.search_in_map));


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.profile:
                if (userModel != null) {
                    if (userModel.getData() != null && userModel.getData().getRole().equals("client")) {
                        navigateToProfileActivity();

                    } else {
                        navigateToProviderProfile();
                    }

                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    intent.putExtra("from", true);
                    startActivity(intent);
                }
                break;


            case R.id.manageProfile:
                if (userModel != null) {
                    navigateToUpdateProviderProfile();

                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    intent.putExtra("from", true);
                    startActivity(intent);
                }
                break;


            case R.id.editProfile:
                if (userModel != null) {
                    if (userModel.getData() != null && userModel.getData().getRole().equals("client")) {
                        navigateToUpdateClientProfileActivity();

                    } else {


                    }

                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    intent.putExtra("from", true);
                    startActivity(intent);
                }
                break;

            case R.id.ads:

                navigateToAdsActivity();
                break;

            case R.id.upgrade:
                if (userModel != null) {
                    if (userModel.getData() != null && userModel.getData().getRole().equals("provider")) {
                        navigateToPaymentActivity();

                    } else {
                        Toast.makeText(this, R.string.prov_only, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    intent.putExtra("from", true);
                    startActivity(intent);
                }
                break;
            case R.id.terms:
                navigateToTermsActivity();
                break;

            case R.id.order:
                if (userModel!=null)
                {
                    navigateToMyOrderActivity();

                }else
                    {
                        Intent intent = new Intent(this, SignInActivity.class);
                        intent.putExtra("from", true);
                        startActivity(intent);
                    }
                break;


            case R.id.lang:
                CreateLangDialogAlert();
                break;

            case R.id.share:
                share();
                break;

            case R.id.logout:
                if (userModel == null) {
                    navigateToSignInActivity();
                } else {
                    logout();

                }
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void navigateToMyOrderActivity() {
        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, MyOrderActivity.class);
                    startActivity(intent);
                },500);
    }

    private void navigateToUpdateProviderProfile() {

        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, UpdateProviderProfileActivity.class);
                    startActivityForResult(intent, 300);
                },500);


    }

    private void navigateToProviderProfile() {

        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, ProviderProfileActivity.class);
                    startActivity(intent);

                },500);


    }

    private void navigateToUpdateClientProfileActivity() {

        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, UpdateClientProfileActivity.class);
                    startActivityForResult(intent, 200);

                },500);




    }

    private void navigateToPaymentActivity() {

        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    startActivity(intent);

                },500);

    }

    private void navigateToAdsActivity() {

        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, AdsActivity.class);
                    startActivity(intent);

                },500);

    }

    private void share() {
        new Handler()
                .postDelayed(() -> {
                    String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(intent);
                }, 500);


    }


    private void CreateLangDialogAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_language, null, false);

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );

        if (lang.equals("ar")) {
            binding.rbAr.setChecked(true);
            binding.rbEn.setChecked(false);
        } else {
            binding.rbAr.setChecked(false);
            binding.rbEn.setChecked(true);
        }
        binding.rbAr.setOnClickListener(view -> {
            new Handler()
                    .postDelayed(() -> {

                                if (!lang.equals("ar")) {
                                    dialog.dismiss();
                                    refreshActivity("ar");

                                }
                            }
                            , 500);
        });

        binding.rbEn.setOnClickListener(view -> {
            new Handler()
                    .postDelayed(() -> {
                        if (!lang.equals("en")) {
                            dialog.dismiss();

                            refreshActivity("en");
                        }

                    }, 500);

        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void refreshActivity(String lang) {
        Paper.init(this);
        Paper.book().write("lang", lang);
        preferences.selectedLanguage(this, lang);
        preferences.saveSelectedLanguage(this);
        LanguageHelper.setNewLocale(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    private void navigateToTermsActivity() {
        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, TermsActivity.class);
                    startActivity(intent);
                }, 500);

    }


    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();

    }

    private void navigateToProfileActivity() {
        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivityForResult(intent, 100);
                }, 500);

    }


    private void logout() {

        preferences.clear(this);
        userModel = null;
        navigateToSignInActivity();

    }

    @Override
    public void onBackPressed() {

        back();
    }

    private void back() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else {
            if (userModel != null) {
                finish();
            } else {
                navigateToSignInActivity();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = manager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            userModel = preferences.getUserData(this);
            updateUi();
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            userModel = preferences.getUserData(this);
            updateUi();
        } else if (requestCode == 300 && resultCode == RESULT_OK && data != null) {
            userModel = preferences.getUserData(this);
            updateUi();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = manager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
