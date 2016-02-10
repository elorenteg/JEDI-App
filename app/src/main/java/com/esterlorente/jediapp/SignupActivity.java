package com.esterlorente.jediapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;

public class SignupActivity extends AppCompatActivity {
    private String TAG = "SIGNUP_ACTIVITY";

    private Context context;
    private LoginHelper loginHelper;

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
                    Toast.makeText(context, "Bienvenido " + name + "!", Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putString("key_username", name); // Storing string
                    editor.commit(); // commit changes

                    createUser(name, pass, email);
                    loginUser(name);
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

    private void loginUser(String username) {
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

    private void createUser(String name, String pass, String email) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.USERNAME, name);
        valuesToStore.put(loginHelper.PASSWORD, pass);
        valuesToStore.put(loginHelper.EMAIL, email);
        valuesToStore.putNull(loginHelper.IMAGE);
        valuesToStore.putNull(loginHelper.STREET);
        loginHelper.createUser(valuesToStore);

        updateLastNotification(name, pass);
    }

    private void updateLastNotification(String name, String pass) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.LAST_NOTIF, "Bienvenido " + name + "!");
        loginHelper.updateLastNotification(valuesToStore, name);
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

        outstate.putString("editName", editName.getText().toString());
        outstate.putString("editEmail", editEmail.getText().toString());
        outstate.putString("editPass", editPass.getText().toString());
        Log.v(TAG, "Guardando estado");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        editName.setText(savedInstanceState.getString("editName"));
        editEmail.setText(savedInstanceState.getString("editEmail"));
        editPass.setText(savedInstanceState.getString("editPass"));
        Log.v(TAG, "Restableciendo estado");
    }
}
