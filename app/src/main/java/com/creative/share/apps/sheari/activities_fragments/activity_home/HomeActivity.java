package com.creative.share.apps.sheari.activities_fragments.activity_home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;

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
import com.creative.share.apps.sheari.activities_fragments.activity_payment.PaymentActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_profile.ProfileActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.sheari.activities_fragments.activity_terms.TermsActivity;
import com.creative.share.apps.sheari.adapters.ViewPagerAdapter;
import com.creative.share.apps.sheari.databinding.DialogLanguageBinding;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
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
                navigateToProfileActivity();
                break;
            case R.id.ads:
                navigateToAdsActivity();
                break;

            case R.id.upgrade:
                navigateToPaymentActivity();
                break;
            case R.id.terms:
                navigateToTermsActivity();
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

    private void navigateToPaymentActivity() {
        Intent intent = new Intent(this, PaymentActivity.class);
        startActivity(intent);
    }

    private void navigateToAdsActivity() {
        Intent intent = new Intent(this, AdsActivity.class);
        startActivity(intent);
    }
    private void share()
    {
        new Handler()
                .postDelayed(() -> {
                    String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(intent);
                }, 1000);


    }

    private void CreateLangDialogAlert()
    {
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

    private void refreshActivity(String lang)
    {
        Paper.init(this);
        Paper.book().write("lang", lang);
        preferences.selectedLanguage(this, lang);
        preferences.saveSelectedLanguage(this);
        LanguageHelper.setNewLocale(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    private void navigateToTermsActivity()
    {
        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, TermsActivity.class);
                    startActivity(intent);
                }, 1000);

    }

    private void navigateToSignInActivity()
    {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();

    }
    private void navigateToProfileActivity()
    {
        new Handler()
                .postDelayed(() -> {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                },500);

    }


    private void logout() {


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
