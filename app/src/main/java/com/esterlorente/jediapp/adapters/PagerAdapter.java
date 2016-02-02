package com.esterlorente.jediapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.esterlorente.jediapp.MemoryFragment;
import com.esterlorente.jediapp.RanquingFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private String TAG = "PAGER_ADAPTER";

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Memory", "Ranking"};
    private Context context;
    Fragment tab = null;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.e(TAG, "Memory Fragment");
                tab = new MemoryFragment();
                break;
            case 1:
                Log.e(TAG, "Ranquing Fragment");
                tab = new RanquingFragment();
                break;
        }
        return tab;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}