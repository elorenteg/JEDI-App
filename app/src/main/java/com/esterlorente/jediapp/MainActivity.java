package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY";

    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private LoginHelper loginHelper;

    private MenuItem prevMenuItem = null;
    private String username;

    private EditText editCalc;
    private Button buttonCalc;

    private EditText editWeird;
    private Button buttonWeird;

    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ///

        buttonCalc = (Button) findViewById(R.id.buttonCalc);
        editCalc = (EditText) findViewById(R.id.editCalc);
        View.OnClickListener lis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CalculadoraActivity.class);
                Bundle bundle = new Bundle();
                String text = editCalc.getText().toString();
                bundle.putString("editTextOper", text);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        buttonCalc.setOnClickListener(lis);

        buttonWeird = (Button) findViewById(R.id.buttonWeird);
        editWeird = (EditText) findViewById(R.id.editWeird);
        View.OnClickListener lis3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameActivity.class);
                String text = editWeird.getText().toString();
                if (!text.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("editNumCards", Integer.valueOf(text));
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        };
        buttonWeird.setOnClickListener(lis3);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        View.OnClickListener lis4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("key_username");
                editor.commit();

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        };
        buttonLogout.setOnClickListener(lis4);
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

                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.menu_music:
                        Toast.makeText(context, "Music selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.menu_game:
                        Toast.makeText(context, "Game Selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, GameActivity.class));
                        break;

                    case R.id.menu_calculator:
                        Toast.makeText(context, "Calculator Selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, CalculadoraActivity.class));
                        break;

                    case R.id.menu_settiongs:
                        Toast.makeText(context, "Settings Selected", Toast.LENGTH_SHORT).show();
                        break;
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
