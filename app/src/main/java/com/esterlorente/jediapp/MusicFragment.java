package com.esterlorente.jediapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.services.MediaPlayerService;

import java.io.File;
import java.io.IOException;

public class MusicFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MUSIC_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private ImageView imageCover;
    private Button buttonPlay, buttonNext, buttonPrevious, buttonStop;
    private ImageButton imagePlay, imageNext, imagePrevious;
    //private MediaPlayer mediaPlayer;

    MediaPlayerService mService;
    boolean bound = false;

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

        //initMediaPlayer();
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

    /*
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        File sdCard = Environment.getExternalStorageDirectory();
        Log.e(TAG, sdCard.getAbsolutePath());
        File song = new File(sdCard.getAbsolutePath() + "/Music/song.mp3");

        try {
            mediaPlayer.setDataSource(song.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
            case R.id.imagePlay:

                if (bound) {
                    mService.play();
                }

                /*
                if (mediaPlayer.isPlaying()) {
                    buttonPlay.setText("Play");
                    //mediaPlayer.pause();
                } else {
                    buttonPlay.setText("Pause");
                    mediaPlayer.start();
                }
                */
                break;
            case R.id.buttonNext:
            case R.id.imageNext:
                break;
            case R.id.buttonPrevious:
            case R.id.imagePrevious:

                break;
            case R.id.buttonStop:
                mService.stop();
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
}