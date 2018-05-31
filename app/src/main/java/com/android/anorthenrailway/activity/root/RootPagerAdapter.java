package com.android.anorthenrailway.activity.root;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.android.anorthenrailway.R;

public class RootPagerAdapter extends FragmentStatePagerAdapter {

    private static Context context;

    public RootPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new RootFragmentHandler().setIndex(RootTabEnum.valueOf(position));
    }

    public void updateTabs(RootTabEnum active) {
        TabLayout tl = (TabLayout) ((Activity)context).findViewById(R.id.aRootTabLayout);

        for (RootTabEnum i: RootTabEnum.values()) {
            TextView tv = ((TextView)tl.getTabAt(i.getValue()).getCustomView());
            if (active == i) {
                tv.setTextColor(ContextCompat.getColor(context, R.color.colourTabFocused));
            }
            else {
                tv.setTextColor(ContextCompat.getColor(context, R.color.colourTabRootNFocused));
            }
            Drawable dr = getIcon(RootTabEnum.valueOf(i.getValue()), active == i);
            tv.setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
        }
    }

    @Override
    public int getCount() {
        return RootTabEnum.values().length;
    }

    private Drawable getIcon(RootTabEnum index, boolean active) {
        Drawable dr = null;
        switch (index) {
            case TAB_HOME:
                dr = ContextCompat.getDrawable(context, active ? R.drawable.tab_home_active: R.drawable.tab_home);
                break;

            case TAB_PLAN_BUY:
                dr = ContextCompat.getDrawable(context, active ? R.drawable.tab_plan_active:  R.drawable.tab_plan);
                break;

            case TAB_LIVE:
                dr = ContextCompat.getDrawable(context, active ? R.drawable.tab_live_active : R.drawable.tab_live);
                break;

            case TAB_MY_TICKETS:
                dr = ContextCompat.getDrawable(context, active ? R.drawable.tab_tickets_active : R.drawable.tab_tickets);
                break;

            case TAB_MORE:
                dr = ContextCompat.getDrawable(context, active ? R.drawable.tab_more_active : R.drawable.tab_more);
                break;
        }
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable img = new BitmapDrawable(((AppCompatActivity)context).getResources(), Bitmap.createScaledBitmap(bitmap, 80, 75, true));
        return img;
    }

    private TextView getTextView(RootTabEnum index, boolean active) {
        TextView tv = new TextView(context);
        tv.setLines(2);
        tv.setMaxLines(2);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ContextCompat.getColor(context, active ? R.color.colourTabFocused : R.color.colourTabNFocused));
        tv.setTextSize(12);

        switch (index) {
            case TAB_HOME:
                tv.setLines(1);
                tv.setMaxLines(1);
                tv.setText(R.string.tab_text_0);
                break;

            case TAB_PLAN_BUY:
                tv.setText(R.string.tab_text_1);
                break;

            case TAB_LIVE:
                tv.setText(R.string.tab_text_2);
                break;

            case TAB_MY_TICKETS:
                tv.setText(R.string.tab_text_3);
                break;

            case TAB_MORE:
                tv.setLines(1);
                tv.setMaxLines(1);
                tv.setText(R.string.tab_text_4);
                break;
        }

        Drawable img = getIcon(index, active);
        tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        return tv;
    }

    public void initTabs(Context context) {
        TabLayout tl = (TabLayout) ((Activity)context).findViewById(R.id.aRootTabLayout);
        tl.getTabAt(RootTabEnum.TAB_HOME.getValue()).setCustomView(getTextView(RootTabEnum.TAB_HOME, true));
        tl.getTabAt(RootTabEnum.TAB_PLAN_BUY.getValue()).setCustomView(getTextView(RootTabEnum.TAB_PLAN_BUY, false));
        tl.getTabAt(RootTabEnum.TAB_LIVE.getValue()).setCustomView(getTextView(RootTabEnum.TAB_LIVE, false));
        tl.getTabAt(RootTabEnum.TAB_MY_TICKETS.getValue()).setCustomView(getTextView(RootTabEnum.TAB_MY_TICKETS, false));
        tl.getTabAt(RootTabEnum.TAB_MORE.getValue()).setCustomView(getTextView(RootTabEnum.TAB_MORE, false));
    }
}
