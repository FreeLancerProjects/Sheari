package com.creative.share.apps.sheari.activities_fragments.activity_my_orders;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Current;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Pending;
import com.creative.share.apps.sheari.activities_fragments.activity_my_orders.fragments.Fragment_Order_Previous;
import com.creative.share.apps.sheari.adapters.ViewPagerAdapter;
import com.creative.share.apps.sheari.databinding.ActivityMyOrderBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MyOrderActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyOrderBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titles;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_order);
        initView();
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        titles = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);



        titles.add(getString(R.string.pending));
        titles.add(getString(R.string.current));
        titles.add(getString(R.string.previous2));

        fragmentList.add(Fragment_Order_Pending.newInstance());
        fragmentList.add(Fragment_Order_Current.newInstance());
        fragmentList.add(Fragment_Order_Previous.newInstance());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addTitles(titles);
        adapter.addFragments(fragmentList);


        binding.pager.setAdapter(adapter);
        binding.tab.setupWithViewPager(binding.pager);

        binding.pager.setOffscreenPageLimit(fragmentList.size());

    }


    public void  updateFragmentCurrent()
    {
        Fragment_Order_Current fragment_order_current = (Fragment_Order_Current) adapter.getItem(1);
        fragment_order_current.getOrders();
    }

    public void  updateFragmentPrevious()
    {
        Fragment_Order_Previous fragment_order_previous = (Fragment_Order_Previous) adapter.getItem(2);
        fragment_order_previous.getOrders();
    }
    @Override
    public void back() {
        finish();
    }
}
