package com.cs3235.nfcsniffer;

/**
 * Created by mingxuan on 5/4/2016.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import android.os.IBinder;
public class Background extends Service {


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        Background getService() {
            return Background.this;
        }
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
