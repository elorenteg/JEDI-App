package com.esterlorente.jediapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esterlorente.jediapp.data.LoginHelper;

import java.io.File;
import java.io.IOException;

public class MusicFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MUSIC_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private ImageView imageCover;
    private Button buttonPlay, buttonNext, buttonPrevious;
    private ImageButton imagePlay, imageNext, imagePrevious;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music, container, false);
        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        initMediaPlayer();
        initView();

        return rootView;
    }

    private void initView() {
        imageCover = (ImageView) rootView.findViewById(R.id.imageCover);

        buttonPlay = (Button) rootView.findViewById(R.id.buttonPlay);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonPrevious = (Button) rootView.findViewById(R.id.buttonPrevious);

        buttonPlay.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);

        imagePlay = (ImageButton) rootView.findViewById(R.id.imagePlay);
        imageNext = (ImageButton) rootView.findViewById(R.id.imageNext);
        imagePrevious = (ImageButton) rootView.findViewById(R.id.imagePrevious);

        imagePlay.setOnClickListener(this);
        imageNext.setOnClickListener(this);
        imagePrevious.setOnClickListener(this);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
            case R.id.imagePlay:
                if (mediaPlayer.isPlaying()) {
                    buttonPlay.setText("Play");
                    mediaPlayer.pause();
                } else {
                    buttonPlay.setText("Pause");
                    mediaPlayer.start();
                }
                break;
            case R.id.buttonNext:
            case R.id.imageNext:
                break;
            case R.id.buttonPrevious:
            case R.id.imagePrevious:

                break;
        }
    }
}