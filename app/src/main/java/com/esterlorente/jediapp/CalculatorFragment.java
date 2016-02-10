package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.MathEval;


public class CalculatorFragment extends Fragment implements View.OnClickListener {
    private String TAG = "CALCULATOR_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private TextView textOper, textRes;
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonPlus, buttonMin, buttonProd, buttonDiv, buttonDot, buttonEqu, buttonDel;


    public CalculatorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        textOper = (TextView) rootView.findViewById(R.id.text_oper);
        textOper.setText("");
        textRes = (TextView) rootView.findViewById(R.id.text_res);
        textRes.setText("");

        if (savedInstanceState != null) {
            textOper.setText(savedInstanceState.getString("textOper"));
            Log.v(TAG, "Restableciendo textOper: " + savedInstanceState.getString("textOper"));

            textRes.setText(savedInstanceState.getString("textRes"));
            Log.v(TAG, "Restableciendo textRes: " + savedInstanceState.getString("textRes"));
        }

        initButtons();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("textOper", textOper.getText().toString());
        outstate.putString("textRes", textRes.getText().toString());
        outstate.putString("title", getActivity().getTitle().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            getActivity().setTitle(savedInstanceState.getString("title"));
        }

        /*
        if (savedInstanceState != null) {
            textOper.setText(savedInstanceState.getString("textOper"));
            Log.v(TAG, "Restableciendo textOper: " + savedInstanceState.getString("textOper"));

            textRes.setText(savedInstanceState.getString("textRes"));
            Log.v(TAG, "Restableciendo textRes: " + savedInstanceState.getString("textRes"));
        }

*/
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
                try {
                    result = evaluateExpression(textOper.getText().toString());
                    textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));
                } catch (ArithmeticException e) {
                }
                break;
            case R.id.button_equ:
                try {
                    result = evaluateExpression(textOper.getText().toString());
                    textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));

                    animateResult(result);
                } catch (ArithmeticException e) {
                    Snackbar snackbar = Snackbar
                            .make(rootView, "Expression error", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }

    private void animateResult(final String result) {
        Animation animOper = AnimationUtils.loadAnimation(context, R.anim.calc_oper);
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

        Animation animRes = AnimationUtils.loadAnimation(context, R.anim.calc_res);
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
    }

    private void initButtons() {
        button0 = (Button) rootView.findViewById(R.id.button_0);
        button1 = (Button) rootView.findViewById(R.id.button_1);
        button2 = (Button) rootView.findViewById(R.id.button_2);
        button3 = (Button) rootView.findViewById(R.id.button_3);
        button4 = (Button) rootView.findViewById(R.id.button_4);
        button5 = (Button) rootView.findViewById(R.id.button_5);
        button6 = (Button) rootView.findViewById(R.id.button_6);
        button7 = (Button) rootView.findViewById(R.id.button_7);
        button8 = (Button) rootView.findViewById(R.id.button_8);
        button9 = (Button) rootView.findViewById(R.id.button_9);
        buttonPlus = (Button) rootView.findViewById(R.id.button_plus);
        buttonMin = (Button) rootView.findViewById(R.id.button_min);
        buttonProd = (Button) rootView.findViewById(R.id.button_prod);
        buttonDiv = (Button) rootView.findViewById(R.id.button_div);
        buttonDot = (Button) rootView.findViewById(R.id.button_dot);
        buttonEqu = (Button) rootView.findViewById(R.id.button_equ);
        buttonDel = (Button) rootView.findViewById(R.id.button_del);

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

    private String evaluateExpression(String text) throws ArithmeticException {
        text = (String) textOper.getText();
        MathEval math = new MathEval();
        double result = -1;
        try {
            result = math.evaluate(text);
        } catch (NumberFormatException | ArithmeticException e) {
            throw new ArithmeticException();
        }
        return String.valueOf(result).replaceAll("\\.0*$", "");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calculator, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
