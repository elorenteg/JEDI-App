package com.esterlorente.jediapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class MyViewPagerAdapter extends ViewPagerAdapter {
    public MyViewPagerAdapter(FragmentManager childFragmentManager) {
        super(childFragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public void addFragment(Fragment fragment, String title) {
        super.addFragment(fragment, title);
    }

    @Override
    public int getItemPosition(Object object) {
        //return POSITION_NONE;
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Fragment getItem(int position) {
        return super.getItem(position);
    }
}
