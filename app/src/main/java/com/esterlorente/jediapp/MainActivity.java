package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY";

    private String FRAGMENT_TAG;
    private Fragment fragment;
    private View rootView;

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
        rootView = findViewById(R.id.content_frame);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }

        if (savedInstanceState != null) {
            setTitle(FRAGMENT_TAG);
            fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        } else {
            setTitle(getString(R.string.profile));

            Bundle b = new Bundle();
            b.putString("username", username);
            fragment = new ProfileFragment();
            fragment.setArguments(b);

            FRAGMENT_TAG = getString(R.string.profile);
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, FRAGMENT_TAG).commit();
        }

        // Anadir Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        loginHelper = new LoginHelper(context);

        initNavigationDrawer();
    }

    private void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final View header = navigationView.getHeaderView(0);
        TextView textUsername = (TextView) header.findViewById(R.id.navview_username);
        textUsername.setText(username);

        Cursor cursor = loginHelper.getUserImageByName(username);
        if (cursor.moveToFirst()) {
            String image = cursor.getString(cursor.getColumnIndex(loginHelper.IMAGE));
            if (image != null) {
                Uri uri = Uri.parse(image);
                //Log.e(TAG, "Usuario no dispone de imagen");

                if (!getRealPathFromURI(uri).equals("-1")) {
                    File imgFile = new File(getRealPathFromURI(uri).toString());
                    if (imgFile.exists()) {
                        final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        updateNavHeader(bitmap);
                    }
                } else {
                    Toast.makeText(context, "Imagen borrada", Toast.LENGTH_SHORT).show();
                }
            }
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
                boolean isNew = true;

                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.menu_profile:
                        Bundle b = new Bundle();
                        b.putString("username", username);
                        f = new ProfileFragment();
                        f.setArguments(b);
                        tag = R.string.profile;
                        break;
                    case R.id.menu_music:
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        f = fragmentManager.findFragmentByTag(getString(R.string.music));
                        if (f == null) {
                            f = new MusicFragment();
                        } else isNew = false;
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
                        notAvailable();
                        break;
                }
                fragment = f;
                FRAGMENT_TAG = getString(tag);
                setTitle(FRAGMENT_TAG);

                if (f != null && isNew == true) {
                    Bundle b = new Bundle();
                    b.putString("username", username);
                    f.setArguments(b);
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

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
            return "-1";
        }
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
                notAvailable();
                return true;
            case R.id.logout:
                logoutUser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        SharedPreferences pref = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
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
        outstate.putString("username", username);
        Log.v(TAG, "Guardando fragment: " + FRAGMENT_TAG);

        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outstate, FRAGMENT_TAG, fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);

        FRAGMENT_TAG = savedInstanceState.getString("fragmentTag");
        username = savedInstanceState.getString("username");
        Log.v(TAG, "Restableciendo fragment: " + FRAGMENT_TAG);

        restoreFragment(savedInstanceState);
    }

    private void restoreFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        setTitle(FRAGMENT_TAG);

        if (savedInstanceState != null) {
            fragment = fragmentManager.getFragment(savedInstanceState, FRAGMENT_TAG);
        } else {
            Bundle b = new Bundle();
            b.putString("username", username);
            fragment = new ProfileFragment();
            fragment.setArguments(b);
            transaction.add(R.id.content_frame, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }

    private void initFragment() {
        setTitle(FRAGMENT_TAG);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public void updateNavHeader(final Bitmap bitmap) {
        final View header = navigationView.getHeaderView(0);
        final ImageView imageUsername = (ImageView) header.findViewById(R.id.navview_image);

        header.post(new Runnable() {
            @Override
            public void run() {
                int width = header.getWidth();
                int height = header.getHeight();

                Bitmap bitmap3 = Bitmap.createScaledBitmap(bitmap, width, height, true);
                //bitmap3 = ImageParser.blurRenderScript(context, bitmap3, 25);
                imageUsername.setImageBitmap(bitmap3);
            }
        });
    }

    private void notAvailable() {
        Snackbar snackbar = Snackbar.make(rootView, "Settings not available", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
