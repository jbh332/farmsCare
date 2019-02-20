package com.example.hong.boaaproject.firstActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private final int ITEM_COUNT = 5;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PagerFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}