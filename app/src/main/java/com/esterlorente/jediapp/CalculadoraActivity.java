package com.esterlorente.jediapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CalculadoraActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "CALCULADORA_ACTIVITY";

    private TextView textOper, textRes;
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
        textRes = (TextView) findViewById(R.id.text_res);
        textRes.setText("");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = bundle.getString("editTextOper");
            Log.e(TAG, "Paso valor: " + value);
            textOper.setText(value);
        } else Log.e(TAG, "Intent - No hay valor");

        initButtons();
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

        outstate.putString("textOper", textOper.getText().toString());
        Log.v(TAG, "Guardando textOper: " + textOper.getText().toString());

        outstate.putString("textRes", textRes.getText().toString());
        Log.v(TAG, "Guardando textRes: " + textRes.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        textOper.setText(savedInstanceState.getString("textOper"));
        Log.v(TAG, "Restableciendo textOper: " + savedInstanceState.getString("textOper"));

        textRes.setText(savedInstanceState.getString("textRes"));
        Log.v(TAG, "Restableciendo textRes: " + savedInstanceState.getString("textRes"));
    }


    @Override
    public void onClick(View v) {
        String text = textOper.getText().toString();

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
            case R.id.button_del:
                if (text.length() > 0)
                    textOper.setText(text.subSequence(0, text.length() - 1));
                else {
                    //TODO: toast
                }
                break;
        }

        final String result;
        switch (v.getId()) {
            case R.id.button_0:
            case R.id.button_1:
            case R.id.button_2:
            case R.id.button_3:
            case R.id.button_4:
            case R.id.button_5:
            case R.id.button_6:
            case R.id.button_7:
            case R.id.button_8:
            case R.id.button_9:
            case R.id.button_dot:
            case R.id.button_del:
                result = evaluateExpression(textOper.getText().toString());
                textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));
                break;
            case R.id.button_equ:
                result = evaluateExpression(textOper.getText().toString());


                Animation animOper = AnimationUtils.loadAnimation(this, R.anim.calc_oper);
                animOper.reset();
                textOper.clearAnimation();
                animOper.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        if (textRes.getText().equals("")) textOper.setText("");
                        else textOper.setText(result);
                    }
                });
                textOper.startAnimation(animOper);

                Animation animRes = AnimationUtils.loadAnimation(this, R.anim.calc_res);
                animRes.reset();
                textRes.clearAnimation();

                animRes.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        textRes.setText("");
                    }
                });
                textRes.startAnimation(animRes);




                /*
                AnimationSet animation = new AnimationSet(true);


                float fromXposition = textOper.getX();
                float toXPosition = textOper.getX();
                float fromYPosition = textOper.getY();
                float toYPosition = textOper.getX()-textOper.getHeight();
                TranslateAnimation transRes = new TranslateAnimation(fromXposition, toXPosition, fromYPosition, toYPosition);
                transRes.setDuration(700);
                animation.addAnimation(transRes);


                float fromXscale = 1.0f;
                float toXscale = 2.0f;
                float fromYscale = 1.0f;
                float toYscale = 2.0f;
                ScaleAnimation scaleRes = new ScaleAnimation(fromXscale, toXscale, fromYscale, toYscale);
                scaleRes.setDuration(700);
                animation.addAnimation(scaleRes);

                textRes.startAnimation(animation);
                */


                break;
        }
    }

    private void initButtons() {
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

    private String evaluateExpression(String text) {
        text = (String) textOper.getText();
        MathEval math = new MathEval();
        Context context = getApplicationContext();
        double result = -1;
        try {
            result = math.evaluate(text);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "NumberFormatException", Toast.LENGTH_SHORT).show();
        } catch (ArithmeticException e) {
            Toast.makeText(context, "ArithmeticException", Toast.LENGTH_SHORT).show();
        }
        return String.valueOf(result).replaceAll("\\.0*$", "");
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
