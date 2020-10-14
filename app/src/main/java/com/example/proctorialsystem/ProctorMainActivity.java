package com.example.proctorialsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.proctorialsystem.components.Dashboard.DashboardFragment;
import com.example.proctorialsystem.components.MainFragmentAdapter;
import com.example.proctorialsystem.components.Reports.ReportsFragment;
import com.example.proctorialsystem.components.Updates.UpdatesFragment;
import com.example.proctorialsystem.login.ProctorLoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ProctorMainActivity extends AppCompatActivity {

    DashboardFragment dashboardFragment;
    ReportsFragment reportsFragment;
    UpdatesFragment updatesFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.contains("JWT_TOKEN")){
            Intent gotoLogin = new Intent(this, ProctorLoginActivity.class);
            startActivity(gotoLogin);
        }

        final ViewPager viewPager = findViewById(R.id.activity_main_viewpager);
        MainFragmentAdapter main_adapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(main_adapter);


        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_navigation_dashboard_:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_navigation_report_generation:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_navigation_updates:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }



    private void setupViewPager(ViewPager viewPager) {
        MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());
        dashboardFragment = new DashboardFragment();
        dashboardFragment.setArguments(getIntent().getExtras());
        reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(getIntent().getExtras());
        updatesFragment = new UpdatesFragment();
        updatesFragment.setArguments(getIntent().getExtras());

        adapter.addFragment(dashboardFragment);
        adapter.addFragment(reportsFragment);
        adapter.addFragment(updatesFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
