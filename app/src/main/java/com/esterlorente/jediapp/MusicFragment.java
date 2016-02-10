package com.esterlorente.jediapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
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
    private ImageButton imagePlay, imageNext, imagePrevious, imageStop;

    private String username;

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

        Bundle args = this.getArguments();
        if (args != null) {
            username = args.getString("username");
        }

        initView();


        return rootView;
    }

    private void initView() {
        imageCover = (ImageView) rootView.findViewById(R.id.imageCover);

        imagePlay = (ImageButton) rootView.findViewById(R.id.imagePlay);
        imageNext = (ImageButton) rootView.findViewById(R.id.imageNext);
        imagePrevious = (ImageButton) rootView.findViewById(R.id.imagePrevious);
        imageStop = (ImageButton) rootView.findViewById(R.id.imageStop);

        imagePlay.setOnClickListener(this);
        imageNext.setOnClickListener(this);
        imagePrevious.setOnClickListener(this);
        imageStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagePlay:

                if (bound) {
                    if (mService.isPlaying()) {
                        //buttonPlay.setText("Play");
                        imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        mService.pause();
                    } else {
                        //buttonPlay.setText("Pause");
                        imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                        mService.play();
                        if (!isNotificationVisible()) sendNotification();
                    }
                }
                break;
            case R.id.imageNext:
                if (bound) {
                    mService.next();
                }
                break;
            case R.id.imagePrevious:
                if (bound) {
                    mService.previous();
                }
                break;
            case R.id.imageStop:
                mService.stop();
                //buttonPlay.setText("Play");
                imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
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


    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("title", getActivity().getTitle().toString());

        outstate.putInt("Position", mService.getTimeMedia());
        outstate.putBoolean("isplaying", mService.isPlaying());
        outstate.putInt("song", mService.getSong());

        mService.stop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            getActivity().setTitle(savedInstanceState.getString("title"));

            int position = savedInstanceState.getInt("Position");
            boolean isPlaying = savedInstanceState.getBoolean("isplaying");
            int song = savedInstanceState.getInt("song");

            startService(position, isPlaying, song);
        }
    }

    private void startService(final int position, final boolean isPlaying, final int song) {
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;

                mService = binder.getService();
                mService.restartMediaPlayer(song, position);
                if (isPlaying) {
                    mService.play();
                    //buttonPlay.setText("Pause");
                    imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }

                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };

    }


    private void updateLastNotification() {
        String songName = mService.getSongName();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.LAST_NOTIF, "Escuchando " + songName);
        loginHelper.updateLastNotification(valuesToStore, username);
    }
}