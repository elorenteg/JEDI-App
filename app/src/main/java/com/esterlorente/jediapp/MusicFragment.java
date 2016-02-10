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
                mService.stop();
                //buttonPlay.setText("Play");
                imagePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
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

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        outstate.putString("title", getActivity().getTitle().toString());

        if (bound && mService.mediaPlayerOn()) {
            outstate.putInt("Position", mService.getTimeMedia());
            outstate.putBoolean("isplaying", mService.isPlaying());
            outstate.putInt("song", mService.getSong());

            mService.stop();
        }
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


    public void updateLastNotification() {
        Log.e(TAG, "Update notification");
        String songName = mService.getSongName();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.LAST_NOTIF, getString(R.string.reproduce_music) + " " + songName);
        loginHelper.updateUserTable(valuesToStore, username);
    }
}