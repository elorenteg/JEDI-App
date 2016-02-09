package com.esterlorente.jediapp.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    private String TAG = "MEDIA_PLAYER_SERVICE";
    private final IBinder mBinder = new MediaPlayerBinder();

    private static MediaPlayer mediaPlayer;
    private static ArrayList<String> songs;

    private int SONG_PLAYING;

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
        initMediaPlayer();
    }

    public void initMediaPlayer() {
        SONG_PLAYING = 0;
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/Music/" + songs.get(SONG_PLAYING));

        mediaPlayer = new MediaPlayer();
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
}


