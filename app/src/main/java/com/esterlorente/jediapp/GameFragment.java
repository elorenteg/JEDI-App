package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esterlorente.jediapp.adapters.MyViewPagerAdapter;
import com.esterlorente.jediapp.data.LoginHelper;


public class GameFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private String TAG = "GAME_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int numCards;

    public GameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game, container, false);
        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) setupViewPager(viewPager);
        viewPager.setOnPageChangeListener(this);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MemoryFragment(), getResources().getText(R.string.memory).toString());
        adapter.addFragment(new RanquingFragment(), getResources().getText(R.string.ranquing).toString());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        MyViewPagerAdapter adapter = (MyViewPagerAdapter) viewPager.getAdapter();
        switch (position) {
            case 1:
                ((RanquingFragment) adapter.getItem(position)).refresh(numCards);
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        MyViewPagerAdapter adapter = (MyViewPagerAdapter) viewPager.getAdapter();
        switch (position) {
            case 1:
                ((RanquingFragment) adapter.getItem(position)).refresh(numCards);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.num2:
                if (numCards != 2) {
                    numCards = 2;
                    refreshFragment();
                }
                return true;
            case R.id.num4:
                if (numCards != 4) {
                    numCards = 4;
                    refreshFragment();
                }
                return true;
            case R.id.num6:
                if (numCards != 6) {
                    numCards = 6;
                    refreshFragment();
                }
                return true;
            case R.id.num8:
                if (numCards != 8) {
                    numCards = 8;
                    refreshFragment();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshFragment() {
        MyViewPagerAdapter adapter = (MyViewPagerAdapter) viewPager.getAdapter();
        int position = tabLayout.getSelectedTabPosition();
        switch (position) {
            case 0:
                ((MemoryFragment) adapter.getItem(position)).restartMemory(numCards);
                break;
            case 1:
                ((MemoryFragment) adapter.getItem(position - 1)).restartMemory(numCards);
                ((RanquingFragment) adapter.getItem(position)).refresh(numCards);
                break;
        }
    }
}
