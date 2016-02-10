package com.esterlorente.jediapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.esterlorente.jediapp.MainActivity;
import com.esterlorente.jediapp.MusicFragment;
import com.esterlorente.jediapp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    private String TAG = "MEDIA_PLAYER_SERVICE";
    private final IBinder mBinder = new MediaPlayerBinder();

    private static MediaPlayer mediaPlayer;
    private static ArrayList<String> songs;

    private int SONG_PLAYING;
    private Context context;
    private int NOTIFICATION_ID = 1;

    public MediaPlayerService() {
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        MediaPlayerService.mediaPlayer = mediaPlayer;
    }

    public void onCreate() {
        songs = new ArrayList();
        songs.add("This is the best day ever - MCR.mp3");
        songs.add("Because of You - TaeYeon & Tiffany.mp3");
        songs.add("If - TaeYeon.mp3");

        context = this;
        initMediaPlayer();
    }

    public void initMediaPlayer() {
        SONG_PLAYING = 0;
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/Music/" + songs.get(SONG_PLAYING));

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                SONG_PLAYING = SONG_PLAYING + 1;
                if (SONG_PLAYING == songs.size()) SONG_PLAYING = 0;
                newSong();
            }
        });

        try {
            mediaPlayer.setDataSource(song.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mediaPlayer.pause();
    }

    public String getSongName() {
        return songs.get(SONG_PLAYING);
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind called");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service started byt startService");
        return START_STICKY;
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

    public void play() {
        if (mediaPlayer == null) initMediaPlayer();

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(context, getSongName(), Toast.LENGTH_SHORT).show();
            if (!isNotificationVisible()) sendNotification();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            deleteNotification();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void next() {
        if (mediaPlayer.isPlaying()) {
            SONG_PLAYING = SONG_PLAYING + 1;
            if (SONG_PLAYING == songs.size()) SONG_PLAYING = 0;

            newSong();
        }
    }

    public void previous() {
        if (mediaPlayer.isPlaying()) {
            SONG_PLAYING = SONG_PLAYING - 1;
            if (SONG_PLAYING == -1) SONG_PLAYING = songs.size() - 1;

            newSong();
        }
    }

    public void newSong() {
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/Music/" + songs.get(SONG_PLAYING));

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(MediaPlayerService.this, Uri.parse(song.getAbsolutePath()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, getSongName(), Toast.LENGTH_SHORT).show();
        sendNotification();

        mediaPlayer.start();
    }


    public int getTimeMedia() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getSong() {
        return SONG_PLAYING;
    }

    public void restartMediaPlayer(int song, int time) {
        SONG_PLAYING = song;
        newSong();
        mediaPlayer.seekTo(time);
    }

    public boolean mediaPlayerOn() {
        return mediaPlayer != null;
    }

    private void sendNotification() {
        //Instanciamos Notification Manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Para la notificaciones, en lugar de crearlas directamente, lo hacemos mediante
        // un Builder/contructor.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.reproduce_music) + " " + getSongName());
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
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }
}


