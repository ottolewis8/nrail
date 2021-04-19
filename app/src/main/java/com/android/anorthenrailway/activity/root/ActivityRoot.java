package com.android.anorthenrailway.activity.root;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.anorthenrailway.ActivityBarcode;
import com.android.anorthenrailway.ActivityTickets;
import com.android.anorthenrailway.R;
import com.android.anorthenrailway.TicketFactory;
import com.android.anorthenrailway.activity.ActivityEnum;
import com.android.anorthenrailway.activity.root.fragment.FragmentHome;
import com.android.anorthenrailway.activity.root.fragment.FragmentLive;
import com.android.anorthenrailway.activity.root.fragment.FragmentMore;
import com.android.anorthenrailway.activity.root.fragment.FragmentMyTickets;
import com.android.anorthenrailway.activity.root.fragment.FragmentPlanBuy;

import java.io.File;

public class ActivityRoot extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FragmentMore fragmentMore;
    private FragmentMyTickets fragmentMyTickets;
    private FragmentHome fragmentHome;
    private FragmentPlanBuy fragmentPlanBuy;
    private FragmentLive fragmentLive;
    public static AppCompatActivity rootOwner = null;
    private static RootTabEnum activeFragment;

    @Override
    public void onResume () {
        super.onResume();
        if (activeFragment == RootTabEnum.TAB_MY_TICKETS) {
            fragmentMyTickets.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode( // Stops keyboard showing on resume
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        rootOwner = this;
        requestPermissions();
        setContentView(R.layout.activity_root);

        Toolbar toolbar = (Toolbar) findViewById(R.id.aRootToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.aRootViewPager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.aRootTabLayout);
        final RootPagerAdapter adapter = new RootPagerAdapter(getSupportFragmentManager(), ActivityRoot.this);
        adapter.initTabs(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.updateTabs(RootTabEnum.valueOf(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition());
                createFragmentAfterInflate(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewPager.setCurrentItem(1);
        TicketFactory.loadSettings();
    }

    private void createFragmentAfterInflate(int index) {
        switch (RootTabEnum.valueOf(index)) {
            case TAB_HOME:
                fragmentHome = new FragmentHome();
                activeFragment = RootTabEnum.TAB_HOME;
                break;
            case TAB_PLAN_BUY:
                fragmentPlanBuy = new FragmentPlanBuy();
                activeFragment = RootTabEnum.TAB_PLAN_BUY;
                break;
            case TAB_LIVE:
                fragmentLive = new FragmentLive();
                activeFragment = RootTabEnum.TAB_LIVE;
                break;
            case TAB_MY_TICKETS:
                fragmentMyTickets = new FragmentMyTickets();
                activeFragment = RootTabEnum.TAB_MY_TICKETS;
                break;
            case TAB_MORE:
                fragmentMore = new FragmentMore();
                activeFragment = RootTabEnum.TAB_MORE;
                break;
        }
    }

    public void changeActivity(ActivityEnum activity) {
        switch (activity) {
            case ROOT:
                break;

            case BARCODE:
                startActivity(new Intent(ActivityRoot.this, ActivityBarcode.class));
                break;

            case TICKETS:
                startActivity(new Intent(ActivityRoot.this, ActivityTickets.class));
                break;
        }
    }

    public void requestPermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        } else {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.id.action_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
