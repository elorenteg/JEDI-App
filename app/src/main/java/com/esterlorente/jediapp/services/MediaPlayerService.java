package com.esterlorente.jediapp.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class MediaPlayerService extends Service {
    private String TAG = "MEDIA_PLAYER_SERVICE";
    private final IBinder mBinder = new MediaPlayerBinder();

    public static MediaPlayer mediaPlayer;

    public MediaPlayerService() {
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        MediaPlayerService.mediaPlayer = mediaPlayer;
    }

    public void onCreate() {
        initMediaPlayer();
    }

    public  void initMediaPlayer() {
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/Music/song.mp3");

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(song.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public int getTimeMedia() {
        return mediaPlayer.getCurrentPosition();
    }

    public void setTimeMedia(int time) {
        mediaPlayer.seekTo(time);
    }
}


