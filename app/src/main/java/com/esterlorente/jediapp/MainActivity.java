package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.esterlorente.jediapp.data.LoginHelper;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY";

    private int FRAGMENT_TAG_INT;
    private String FRAGMENT_TAG;
    private Fragment fragment;

    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
    public DrawerLayout drawerLayout;
    private LoginHelper loginHelper;

    private MenuItem prevMenuItem = null;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        /*
        if (savedInstanceState == null) {
            fragment = new ProfileFragment();
            FRAGMENT_TAG = R.string.profile;
            initFragment();
        }
        */

        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        } else {
            fragment = new ProfileFragment();
            FRAGMENT_TAG = getString(R.string.profile);
            Log.e(TAG, FRAGMENT_TAG);
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, FRAGMENT_TAG).commit();
        }

        // Anadir Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        loginHelper = new LoginHelper(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }

        initNavigationDrawer();
    }

    private void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View header = navigationView.getHeaderView(0);
        TextView textUsername = (TextView) header.findViewById(R.id.navview_username);
        textUsername.setText(username);

        ImageView imageUsername = (ImageView) header.findViewById(R.id.navview_image);
        Cursor cursor = loginHelper.getUserImageByName(username);
        if (cursor.moveToFirst()) {
            if (!cursor.isNull(0)) {
                Log.e(TAG, "Cursor con fila y valor");
                byte[] image = cursor.getBlob(1);
                imageUsername.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            } else Log.e(TAG, "Cursor con fila, pero valor null");
        }

        // Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) { // prevMenuItem = menuItem
                    menuItem.setChecked(false);
                    prevMenuItem = null;
                } else { // prevMenuItem = null or another menuItem
                    menuItem.setChecked(true);
                    if (prevMenuItem != null) prevMenuItem.setChecked(false);
                    prevMenuItem = menuItem;
                }

                // Closing drawer on item click
                drawerLayout.closeDrawers();

                Fragment f = null;
                int tag = -1;


                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.menu_profile:
                        f = new ProfileFragment();
                        tag = R.string.profile;
                        break;
                    case R.id.menu_music:
                        f = new MusicFragment();
                        tag = R.string.music;
                        break;
                    case R.id.menu_game:
                        f = new GameFragment();
                        tag = R.string.game;
                        break;
                    case R.id.menu_calculator:
                        f = new CalculatorFragment();
                        tag = R.string.calculator;
                        break;
                    case R.id.menu_settings:
                        break;
                }

                if (f != null) {
                    fragment = f;
                    FRAGMENT_TAG = getString(tag);
                    setTitle(FRAGMENT_TAG);
                    initFragment();
                }


                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // TODO: settings
                return true;
            case R.id.logout:
                logoutUser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("key_username");
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("fragmentTag", FRAGMENT_TAG);
        Log.v(TAG, "Guardando fragment: " + FRAGMENT_TAG);

        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outstate, FRAGMENT_TAG, fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        FRAGMENT_TAG = savedInstanceState.getString("fragmentTag");
        Log.v(TAG, "Restableciendo fragment: " + FRAGMENT_TAG);

        restoreFragment(savedInstanceState);
    }

    private void restoreFragment(Bundle savedInstanceState) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (savedInstanceState != null) {
            fragment = (Fragment) manager.getFragment(savedInstanceState, FRAGMENT_TAG);
        } else {
            fragment = new ProfileFragment();
            transaction.add(R.id.content_frame, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, FRAGMENT_TAG.toString());
        fragmentTransaction.commit();
    }
}
