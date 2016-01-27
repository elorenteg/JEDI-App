package com.esterlorente.jediapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MAIN_ACTIVITY_TAG";

    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }

        // Anadir Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        View.OnClickListener lis = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), CalculadoraActivity.class);
                Bundle bundle = new Bundle();
                String text = editText.getText().toString();
                bundle.putString("editTextOper", text);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        button.setOnClickListener(lis);
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
            case R.id.pizza:
                giveMeAPizza();
                return true;
            case R.id.burger:
                giveMeABurger();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void giveMeABurger() {
        Log.v("Burger", "Give me a burger!");
    }

    private void giveMeAPizza() {
        Log.v("Pizza", "Give me a pizza!");
    }


}
