package com.huongtt.fluxrssdemo;

import android.app.Application;

public class FluxRSSApplication extends Application {
 
    public static final String TAG = FluxRSSApplication.class
            .getSimpleName();
 
 
    private static FluxRSSApplication mInstance;
 
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
 
    public static synchronized FluxRSSApplication getInstance() {
        return mInstance;
    }
 
}