package com.mirakiphi.moztrip.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mirakiphi.moztrip.screens.HorizontalPagerFragment;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private String cityName;
    public MainPagerAdapter(final FragmentManager fm, String cityName) {
        super(fm);
        this.cityName = cityName;
    }

    @Override
    public Fragment getItem(final int position) {
        Bundle bundle = new Bundle();
        bundle.putString("cityName",cityName);
        HorizontalPagerFragment frag = new HorizontalPagerFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
