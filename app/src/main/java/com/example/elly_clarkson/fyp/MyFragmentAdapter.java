package com.example.elly_clarkson.fyp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> list;

        public MyFragmentAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.list=list;
    }
        @Override
        public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

        @Override
        public int getCount() {
        return list.size();
    }

    }
