package com.esterlorente.jediapp;

import android.content.Context;
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

    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
    public DrawerLayout drawerLayout;
    private LoginHelper loginHelper;

    private Fragment fragment;
    private MenuItem prevMenuItem = null;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        if (fragment == null) fragment = new ProfileFragment();
        initFragment(fragment);

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

                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.menu_profile:
                        //Toast.makeText(context, "Profile selected", Toast.LENGTH_SHORT).show();
                        setTitle(getString(R.string.profile));
                        f = new ProfileFragment();
                        break;

                    case R.id.menu_music:
                        //Toast.makeText(context, "Music selected", Toast.LENGTH_SHORT).show();
                        f = new MusicFragment();
                        break;

                    case R.id.menu_game:
                        //Toast.makeText(context, "Game Selected", Toast.LENGTH_SHORT).show();
                        f = new GameFragment();
                        break;

                    case R.id.menu_calculator:
                        //Toast.makeText(context, "Calculator Selected", Toast.LENGTH_SHORT).show();
                        f = new CalculatorFragment();
                        break;

                    case R.id.menu_settings:
                        //Toast.makeText(context, "Settings Selected", Toast.LENGTH_SHORT).show();
                        break;
                }

                if (f != null) {
                    fragment = f;
                    initFragment(f);
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
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("fragmentTag", fragment.getTag());
        Log.v(TAG, "Guardando fragment: " + fragment.getTag());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String fragmentTag = savedInstanceState.getString("fragmentTag");
        restoreFragment(fragmentTag);

        Log.v(TAG, "Restableciendo fragment: " + fragmentTag);
    }

    private void restoreFragment(String fragmentTag) {
        switch (fragmentTag) {
            case "ProfileFragment":
                //Toast.makeText(context, "Profile selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.profile));
                if (fragment != null && !fragmentTag.equals(fragment.getTag()))
                    fragment = new ProfileFragment();
                break;

            case "MusicFragment":
                //Toast.makeText(context, "Music selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.music));
                if (fragment != null && !fragmentTag.equals(fragment.getTag()))
                    fragment = new MusicFragment();
                break;

            case "GameFragment":
                //Toast.makeText(context, "Game Selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.game));
                if (fragment != null && !fragmentTag.equals(fragment.getTag()))
                    fragment = new GameFragment();
                break;

            case "CalculatorFragment":
                //Toast.makeText(context, "Calculator Selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.calculator));
                if (fragment != null && !fragmentTag.equals(fragment.getTag()))
                    fragment = new CalculatorFragment();
                break;

            case "Settings":
                //Toast.makeText(context, "Settings Selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.settings));
                break;
        }

        initFragment(fragment);
    }

    private void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}
