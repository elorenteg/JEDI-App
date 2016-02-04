package com.esterlorente.jediapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.services.MediaPlayerService;

public class MusicFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MUSIC_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private ImageView imageCover;
    private Button buttonPlay, buttonNext, buttonPrevious, buttonStop;
    private ImageButton imagePlay, imageNext, imagePrevious;
    //private MediaPlayer mediaPlayer;

    private MediaPlayerService mService;
    private boolean bound = false;
    private int NOTIFICATION_ID = 1;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;

            mService = binder.getService();

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music, container, false);
        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        initView();

        return rootView;
    }

    private void initView() {
        imageCover = (ImageView) rootView.findViewById(R.id.imageCover);

        buttonPlay = (Button) rootView.findViewById(R.id.buttonPlay);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonPrevious = (Button) rootView.findViewById(R.id.buttonPrevious);
        buttonStop = (Button) rootView.findViewById(R.id.buttonStop);

        buttonPlay.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        imagePlay = (ImageButton) rootView.findViewById(R.id.imagePlay);
        imageNext = (ImageButton) rootView.findViewById(R.id.imageNext);
        imagePrevious = (ImageButton) rootView.findViewById(R.id.imagePrevious);

        imagePlay.setOnClickListener(this);
        imageNext.setOnClickListener(this);
        imagePrevious.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
            case R.id.imagePlay:

                if (bound) {
                    if (mService.mediaPlayer.isPlaying()) {
                        buttonPlay.setText("Play");
                        mService.pause();
                    } else {
                        buttonPlay.setText("Pause");
                        mService.play();
                        if (!isNotificationVisible()) sendNotification();
                    }
                }
                break;
            case R.id.buttonNext:
            case R.id.imageNext:
                break;
            case R.id.buttonPrevious:
            case R.id.imagePrevious:
                break;
            case R.id.buttonStop:
                mService.stop();
                buttonPlay.setText("Play");
                deleteNotification();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        ((MainActivity) getActivity()).startBindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            ((MainActivity) getActivity()).unBindService(mConnection);
            //unbindService(mConnection);
            bound = false;
        }
    }

    private void sendNotification() {
        //Instanciamos Notification Manager
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Para la notificaciones, en lugar de crearlas directamente, lo hacemos mediante
        // un Builder/contructor.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.reproduce_music));
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

    private void deleteNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }
}