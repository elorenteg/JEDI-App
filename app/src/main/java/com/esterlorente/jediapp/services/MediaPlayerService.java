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

public class MediaPlayerService extends Service {
    private final IBinder mBinder = new MediaPlayerBinder();

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public MediaPlayerService() {
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void play() {
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/Music/song.mp3");

        try {
            mediaPlayer.setDataSource(song.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.stop();
    }

}


