package com.example.proctorialsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.proctorialsystem.components.DashboardFragment;
import com.example.proctorialsystem.components.MainFragmentAdapter;
import com.example.proctorialsystem.components.MessagingFragment;
import com.example.proctorialsystem.components.MetricsFragment;
import com.example.proctorialsystem.components.ReportsFragment;
import com.example.proctorialsystem.components.UpdatesFragment;
import com.example.proctorialsystem.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    DashboardFragment dashboardFragment;
    MessagingFragment messagingFragment;
    ReportsFragment reportsFragment;
    UpdatesFragment updatesFragment;
    MetricsFragment metricsFragment;

    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        final ViewPager viewPager = findViewById(R.id.activity_main_viewpager);
        MainFragmentAdapter main_adapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(main_adapter);


        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.action_navigation_dashboard_:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_navigation_messaging:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_navigation_report_generation:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_navigation_updates:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.action_navigation_metrics:
                        viewPager.setCurrentItem(4);
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
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
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
        messagingFragment = new MessagingFragment();
        reportsFragment = new ReportsFragment();
        updatesFragment = new UpdatesFragment();
        metricsFragment = new MetricsFragment();
        adapter.addFragment(dashboardFragment);
        adapter.addFragment(messagingFragment);
        adapter.addFragment(reportsFragment);
        adapter.addFragment(updatesFragment);
        adapter.addFragment(metricsFragment);
        viewPager.setAdapter(adapter);
    }



}
