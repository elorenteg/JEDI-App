package com.esterlorente.jediapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private int NOTIFICATION_BAR = 0;
    private int NOTIFICATION_TOAST = 1;
    private int NOTIFICATION_SNACKBAR = 2;
    private int TYPE_NOTIFICATIONS;
    private int NOTIFICATION_ID = 2;

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

        initButtons();

        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        TYPE_NOTIFICATIONS = pref.getInt("type_notifications", NOTIFICATION_SNACKBAR);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("textOper", textOper.getText().toString());
        outstate.putString("textRes", textRes.getText().toString());
        outstate.putString("title", getActivity().getTitle().toString());

        // Save type notification
        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putInt("type_notifications", TYPE_NOTIFICATIONS);
        editor.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            getActivity().setTitle(savedInstanceState.getString("title"));

            textOper.setText(savedInstanceState.getString("textOper"));
            textRes.setText(savedInstanceState.getString("textRes"));
        }
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
                try {
                    result = evaluateExpression(textOper.getText().toString());
                    textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));
                } catch (ArithmeticException e) {
                    Log.e(TAG, "error");
                }
                break;
            case R.id.button_del:
                if (textOper.getText().toString().equals("")) {
                    textRes.setText("");
                } else {
                    try {
                        result = evaluateExpression(textOper.getText().toString());
                        textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));
                    } catch (ArithmeticException e) {
                    }
                }
                break;
            case R.id.button_equ:
                try {
                    result = evaluateExpression(textOper.getText().toString());
                    textRes.setText(String.valueOf(result).replaceAll("\\.0*$", ""));

                    animateResult(result);
                } catch (ArithmeticException e) {
                    showNotification("Expression error");
                }
                break;
        }
    }

    private void showNotification(String message) {
        //Instanciamos Notification Manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Para la notificaciones, en lugar de crearlas directamente, lo hacemos mediante
        // un Builder/contructor.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message);
        // Creamos un intent explicito, para abrir la app desde nuestra notificación
        Intent resultIntent = new Intent(context, MusicFragment.class);
        //Generamos la backstack y le añadimos el intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        //Obtenemos el pending intent
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        // mId es un identificador que nos permitirá actualizar la notificación
        // más adelante
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toasts:
                TYPE_NOTIFICATIONS = NOTIFICATION_TOAST;
                return true;
            case R.id.state:
                TYPE_NOTIFICATIONS = NOTIFICATION_BAR;
                return true;
            case R.id.snack:
                TYPE_NOTIFICATIONS = NOTIFICATION_SNACKBAR;
                return true;
            case R.id.phone:
                callNumber();
                return true;
            case R.id.browser:
                openBrowser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"));
        startActivity(intent);
    }

    private void openBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon3 : R.drawable.gato;
    }
}
