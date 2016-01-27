package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CalculadoraActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ACTIVITY_2_TAG";

    private TextView textOper;
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonPlus, buttonMin, buttonProd, buttonDiv, buttonDot, buttonEqu, buttonDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        // Anadir Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intent intent = new Intent(getApplicationContext(), BanderaActivity.class);
        //startActivity(intent);

        textOper = (TextView) findViewById(R.id.text_oper);
        textOper.setText("");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = bundle.getString("editTextOper");
            Log.e(TAG, value);
            textOper.setText(value);
        }
        else Log.e(TAG, "Intent - No hay valor");

        button0 = (Button) findViewById(R.id.button_0);
        button1 = (Button) findViewById(R.id.button_1);
        button2 = (Button) findViewById(R.id.button_2);
        button3 = (Button) findViewById(R.id.button_3);
        button4 = (Button) findViewById(R.id.button_4);
        button5 = (Button) findViewById(R.id.button_5);
        button6 = (Button) findViewById(R.id.button_6);
        button7 = (Button) findViewById(R.id.button_7);
        button8 = (Button) findViewById(R.id.button_8);
        button9 = (Button) findViewById(R.id.button_9);
        buttonPlus = (Button) findViewById(R.id.button_plus);
        buttonMin = (Button) findViewById(R.id.button_min);
        buttonProd = (Button) findViewById(R.id.button_prod);
        buttonDiv = (Button) findViewById(R.id.button_div);
        buttonDot = (Button) findViewById(R.id.button_dot);
        buttonEqu = (Button) findViewById(R.id.button_equ);
        buttonDel = (Button) findViewById(R.id.button_del);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);
        buttonMin.setOnClickListener(this);
        buttonProd.setOnClickListener(this);
        buttonDiv.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        buttonEqu.setOnClickListener(this);
        buttonDel.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.exit:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:934137660"));
                startActivity(intent);
                /*
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this will clear all the stack
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
                */
                /*
                Intent i = new Intent(this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                */
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        TextView t = (TextView) findViewById(R.id.text_oper);
        outstate.putString("textOper", t.getText().toString());
        Log.v(TAG, "Guardando resultado: " + t.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        TextView t = (TextView) findViewById(R.id.text_oper);
        t.setText(savedInstanceState.getString("textOper"));
        Log.v(TAG, "Restableciendo resultado: " + savedInstanceState.getString("textOper"));
    }


    @Override
    public void onClick(View v) {
        String text = (String) textOper.getText();
        switch (v.getId()) {
            case R.id.button_0:
                textOper.setText(text + "0");
                break;
            case R.id.button_1:
                textOper.setText(text + "1");
                break;
            case R.id.button_2:
                textOper.setText(text + "2");
                break;
            case R.id.button_3:
                textOper.setText(text + "3");
                break;
            case R.id.button_4:
                textOper.setText(text + "4");
                break;
            case R.id.button_5:
                textOper.setText(text + "5");
                break;
            case R.id.button_6:
                textOper.setText(text + "6");
                break;
            case R.id.button_7:
                textOper.setText(text + "7");
                break;
            case R.id.button_8:
                textOper.setText(text + "8");
                break;
            case R.id.button_9:
                textOper.setText(text + "9");
                break;
            case R.id.button_plus:
                textOper.setText(text + "+");
                break;
            case R.id.button_min:
                textOper.setText(text + "-");
                break;
            case R.id.button_prod:
                textOper.setText(text + "*");
                break;
            case R.id.button_div:
                textOper.setText(text + "/");
                break;
            case R.id.button_dot:
                textOper.setText(text + ".");
                break;
            case R.id.button_equ:
                try {
                    textOper.setText(String.valueOf(new Parser().parse((String) text)));
                } catch (RuntimeException e) {
                    Context context = getApplicationContext();
                    CharSequence toastText = "Invalid Expression";
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_del:
                if (text.length() > 0)
                    textOper.setText(text.subSequence(0, text.length() - 1));
                break;
        }
    }


    // asignacion listeners recursivo
    void assignClickLHandler(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); ++i) {
            if (root.getChildAt(i) instanceof ViewGroup) {
                assignClickLHandler((ViewGroup) root.getChildAt(i));
            } else {
                (root.getChildAt(i)).setOnClickListener(this);
            }
        }
    }


}
