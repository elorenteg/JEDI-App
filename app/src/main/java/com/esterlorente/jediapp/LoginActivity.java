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

import com.crashlytics.android.Crashlytics;
import com.esterlorente.jediapp.data.LoginHelper;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LOGIN_ACTIVITY";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RzbU4RFogQeb5eHn2cxUcXSBH";
    private static final String TWITTER_SECRET = "WKg20eahFPg7NIjzlI9RbEuIhjk2vn8QQ1ZUm1K2HVqDMeO5RR";

    private Context context;
    private LoginHelper loginHelper;

    private EditText editName;
    private EditText editPass;
    private Button buttonLogin;

    private TwitterLoginButton loginButton;

    private TextView textNoAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        initLoginTwitter();

        loginHelper = new LoginHelper(context);
        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        editName = (EditText) findViewById(R.id.editName);
        editPass = (EditText) findViewById(R.id.editPass);
        View.OnClickListener lis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String pass = editPass.getText().toString();

                if (validateUser(name, pass)) {
                    //Toast.makeText(context, "Login correcto!", Toast.LENGTH_SHORT).show();

                    editor.putString("key_username", name);
                    editor.commit();

                    loginUser(name);
                }
            }
        };
        buttonLogin.setOnClickListener(lis);

        textNoAccount = (TextView) findViewById(R.id.textNoAccount);
        View.OnClickListener lis2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignupActivity.class);
                startActivity(intent);
            }
        };
        textNoAccount.setOnClickListener(lis2);


        String username = pref.getString("key_username", null);
        if (username != null) {
            loginUser(username);
        }
    }

    private void loginUser(String username) {
        Intent intent = new Intent(context, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private boolean validateUser(String username, String password) {
        boolean valid = true;

        if (username.isEmpty() || username.contains("@")) {
            editName.setError("enter a valid username");
            valid = false;
        } else editName.setError(null);

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else editPass.setError(null);

        if (valid) {
            Cursor cursor = loginHelper.getUserByName(username);
            if (cursor.moveToFirst()) {
                // User exists
                editName.setError(null);

                String passSaved = cursor.getString(cursor.getColumnIndex(loginHelper.PASSWORD));
                if (!password.equals(passSaved)) {
                    editPass.setError("invalid password");
                    valid = false;
                } else editPass.setError(null);

            } else {
                editName.setError("invalid username");
                valid = false;
            }
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("editName", editName.getText().toString());
        outstate.putString("editPass", editPass.getText().toString());
        Log.v(TAG, "Guardando estado");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        editName.setText(savedInstanceState.getString("editName"));
        editPass.setText(savedInstanceState.getString("editPass"));
        Log.v(TAG, "Restableciendo estado");
    }

    public void initLoginTwitter() {
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;

                String name = "@" + session.getUserName();

                SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                editor.putString("key_username", name);
                editor.commit();

                createUser(name, "", "");
                loginUser(name);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
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
        valuesToStore.put(loginHelper.LAST_NOTIF, getString(R.string.welcome) + name + "!");
        loginHelper.updateUserTable(valuesToStore, name);
    }

}
