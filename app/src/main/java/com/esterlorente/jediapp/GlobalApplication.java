package com.esterlorente.jediapp;


import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.esterlorente.jediapp.services.MediaPlayerService;

public class GlobalApplication extends Application {
    private String TAG = "GLOBAL_STATE";

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

    public MediaPlayerService getmService() {
        return mService;
    }

    public boolean isBound() {
        return bound;
    }

    public ServiceConnection getmConnection() {
        return mConnection;
    }
}