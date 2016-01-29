package com.esterlorente.jediapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY";

    private Context context;
    private LoginHelper loginHelper;

    private EditText editName;
    private EditText editPass;
    private Button buttonInsert;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("Exit me", false)) {
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }

        // Anadir Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        loginHelper = new LoginHelper(context);


        buttonInsert = (Button) findViewById(R.id.buttonInsert);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        editName = (EditText) findViewById(R.id.editName);
        editPass = (EditText) findViewById(R.id.editPass);
        View.OnClickListener lis3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MemoryActivity.class);
                String name = editName.getText().toString();
                String pass = editPass.getText().toString();

                if (!userExists(name)) {
                    Toast.makeText(context, "Nuevo usuario: " + name + " + " + pass, Toast.LENGTH_SHORT).show();
                    createUser(name, pass);
                } else {
                    Toast.makeText(context, "Usuario ya existe con ese username", Toast.LENGTH_SHORT).show();
                }
            }
        };
        buttonInsert.setOnClickListener(lis3);

        View.OnClickListener lis4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MemoryActivity.class);
                String name = editName.getText().toString();
                String pass = editPass.getText().toString();

                if (userCorrect(name, pass)) {
                    Toast.makeText(context, "Login correcto!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, LogedActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        };
        buttonLogin.setOnClickListener(lis4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_menu, menu);
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

    private boolean userExists(String name) {
        Cursor cursor = loginHelper.getUserByName(name);
        return cursor.moveToFirst();
    }

    private boolean userCorrect(String name, String pass) {
        Cursor cursor = loginHelper.getUserByName(name);
        if (cursor.moveToFirst()) {
            // User exists
            String passSaved = cursor.getString(cursor.getColumnIndex("password"));
            return pass.equals(passSaved);
        }
        return false;
    }

    private void createUser(String name, String pass) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put("username", name);
        valuesToStore.put("password", pass);
        loginHelper.createUser(valuesToStore, "USER");
    }
}
