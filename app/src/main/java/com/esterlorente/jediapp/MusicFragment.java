package com.esterlorente.jediapp;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.e(TAG, "onServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;

            mService = binder.getService();

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e(TAG, "onServiceDisconnected");
            bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
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
                    if (mService.isPlaying()) { // esta sonando, poner a pausa
                        Log.e(TAG, "Pausando");
                        imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        mService.pause();
                    } else { // esta en pausa, poner a play
                        Log.e(TAG, "Playeando");
                        imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                        mService.play();
                    }

                    // save notification
                    updateLastNotification();
                }
                break;
            case R.id.imageNext:
                if (bound) {
                    mService.next();
                    updateLastNotification();
                }
                break;
            case R.id.imagePrevious:
                if (bound) {
                    mService.previous();
                    updateLastNotification();
                }
                break;
            case R.id.imageStop:
                if (bound) {
                    mService.stop();
                    imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        Log.e(TAG, "onSaveInstanceState");

        outstate.putString("title", getActivity().getTitle().toString());

        if (bound && mService.mediaPlayerOn()) {
            outstate.putInt("Position", mService.getTimeMedia());
            outstate.putBoolean("isplaying", mService.isPlaying());
            outstate.putInt("song", mService.getSong());

            //mService.stop();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");

        if (savedInstanceState != null) {
            getActivity().setTitle(savedInstanceState.getString("title"));

            int position = savedInstanceState.getInt("Position");
            boolean isPlaying = savedInstanceState.getBoolean("isplaying");
            int song = savedInstanceState.getInt("song");

            //initMediaPlayer();

            if (isPlaying) imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));

            //startService(position, isPlaying, song);
        }
    }

    public void updateLastNotification() {
        String songName = mService.getSongName();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.LAST_NOTIF, getString(R.string.reproduce_music) + " " + songName);
        loginHelper.updateUserTable(valuesToStore, username);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e(TAG, "onStart");

        setRetainInstance(true);
        Intent intent = new Intent(getActivity().getApplicationContext(), MediaPlayerService.class);
        getActivity().getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        if (bound) {
            getActivity().getApplicationContext().unbindService(mConnection);
            bound = false;
        }
    }
}