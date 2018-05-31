package com.android.anorthenrailway.activity.root;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.anorthenrailway.R;


public class RootFragmentHandler extends Fragment {
    private RootTabEnum index = RootTabEnum.TAB_HOME;

    public RootFragmentHandler() {
    }

    public RootFragmentHandler setIndex(RootTabEnum i) {
        index = i;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (index) {
            case TAB_HOME: return inflater.inflate(R.layout.fragment_home, container, false);
            case TAB_PLAN_BUY: return inflater.inflate(R.layout.fragment_plan_buy, container, false);
            case TAB_LIVE: return inflater.inflate(R.layout.fragment_live, container, false);
            case TAB_MY_TICKETS: return inflater.inflate(R.layout.fragment_my_tickets, container, false);
            case TAB_MORE: return inflater.inflate(R.layout.fragment_more, container, false);
            default: return null;
        }
    }
}