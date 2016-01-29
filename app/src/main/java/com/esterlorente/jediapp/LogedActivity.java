package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.esterlorente.jediapp.data.LoginHelper;

public class LogedActivity extends AppCompatActivity {
    private String TAG = "LOGED_ACTIVITY";

    private Context context;
    private LoginHelper loginHelper;

    private EditText editCalc;
    private EditText editMemory;
    private Button buttonCalc;
    private Button buttonMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged);

        // Anadir Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

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

        buttonMemory = (Button) findViewById(R.id.buttonMemory);
        editMemory = (EditText) findViewById(R.id.editMemory);
        View.OnClickListener lis2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MemoryActivity.class);
                String text = editMemory.getText().toString();
                if (!text.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("editNumCards", Integer.valueOf(text));
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        };
        buttonMemory.setOnClickListener(lis2);
    }

}
