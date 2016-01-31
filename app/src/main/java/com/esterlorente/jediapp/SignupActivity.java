package com.esterlorente.jediapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

public class SignupActivity extends AppCompatActivity {
    private String TAG = "LOGIN_ACTIVITY";

    private Context context;
    private LoginHelper loginHelper;

    private String username = null;

    private EditText editName;
    private EditText editPass;
    private EditText editEmail;
    private Button buttonCreateAccount;

    private TextView textAlreadyMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = getApplicationContext();

        loginHelper = new LoginHelper(context);

        buttonCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPass = (EditText) findViewById(R.id.editPass);
        View.OnClickListener lis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String pass = editPass.getText().toString();

                if (validateNewUser(name, pass, email)) {
                    // Datos validos
                    Toast.makeText(context, "Nuevo usuario: " + name + " + " + pass, Toast.LENGTH_SHORT).show();
                    createUser(name, pass);
                    loginUser();
                }
            }
        };
        buttonCreateAccount.setOnClickListener(lis);

        textAlreadyMember = (TextView) findViewById(R.id.textAlreadyMember);
        View.OnClickListener lis2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        };
        textAlreadyMember.setOnClickListener(lis2);
    }

    private void loginUser() {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean userExists(String name) {
        Cursor cursor = loginHelper.getUserByName(name);
        return cursor.moveToFirst();
    }

    private void createUser(String name, String pass) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put("username", name);
        valuesToStore.put("password", pass);
        loginHelper.createUser(valuesToStore, "USER");
    }

    private boolean validateNewUser(String username, String password, String email) {
        boolean valid = true;

        if (username.isEmpty()) {
            editName.setError("enter a valid username");
            valid = false;
        } else if (userExists(username)) {
            editName.setError("user already exists");
            valid = false;
        } else {
            editName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("enter a valid email address");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editPass.setError(null);
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("username", username);
        Log.v(TAG, "Guardando username: " + username);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        username = (savedInstanceState.getString("username"));
        Log.v(TAG, "Restableciendo username: " + savedInstanceState.getString("username"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.simple_menu, menu);
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
}