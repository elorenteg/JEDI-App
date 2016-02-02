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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY";

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

        //Creamos el primer fragment, y no le pasamos argumentos!

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        //Reemplazamos el Frame Layout de la Activity por el nuevo fragment.
        //El Frame Layout es el contenedor
        fragmentTransaction.replace(R.id.content_frame, new ProfileFragment());
        fragmentTransaction.commit();

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
                        Toast.makeText(context, "Profile selected", Toast.LENGTH_SHORT).show();
                        f = new ProfileFragment();
                        break;

                    case R.id.menu_music:
                        Toast.makeText(context, "Music selected", Toast.LENGTH_SHORT).show();
                        f = new MusicFragment();
                        break;

                    case R.id.menu_game:
                        Toast.makeText(context, "Game Selected", Toast.LENGTH_SHORT).show();
                        f = new GameFragment();
                        break;

                    case R.id.menu_calculator:
                        Toast.makeText(context, "Calculator Selected", Toast.LENGTH_SHORT).show();
                        f = new CalculatorFragment();
                        break;

                    case R.id.menu_settiongs:
                        Toast.makeText(context, "Settings Selected", Toast.LENGTH_SHORT).show();
                        break;
                }

                if (f != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, f);
                    fragmentTransaction.commit();
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

}
