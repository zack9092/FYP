package com.example.elly_clarkson.fyp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> list;


        public MyFragmentAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.list=list;
        notifyDataSetChanged();
    }


        @Override
        public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

        @Override
        public int getCount() {
        return list.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }




    }

