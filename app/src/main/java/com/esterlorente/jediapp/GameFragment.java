package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
        Bundle args = this.getArguments();
        String username = null;
        if (args != null) {
            username = args.getString("username");
        }
        Bundle b = new Bundle();
        b.putString("username", username);

        MemoryFragment memoryFragment = new MemoryFragment();
        memoryFragment.setArguments(b);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(memoryFragment, getResources().getText(R.string.memory).toString());
        adapter.addFragment(new RanquingFragment(), getResources().getText(R.string.ranquing).toString());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");
        outstate.putInt("numCards", numCards);
        outstate.putString("title", getActivity().getTitle().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            numCards = savedInstanceState.getInt("numCards");
            getActivity().setTitle(savedInstanceState.getString("title"));
        }
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
                //((RanquingFragment) adapter.getItem(position)).refresh(numCards);
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        MyViewPagerAdapter adapter = (MyViewPagerAdapter) viewPager.getAdapter();
        switch (position) {
            case 1:
                //((RanquingFragment) adapter.getItem(position)).refresh(numCards);
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
                numCards = 2;
                return true;
            case R.id.num4:
                numCards = 4;
                return true;
            case R.id.num6:
                numCards = 6;
                return true;
            case R.id.num8:
                numCards = 8;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
