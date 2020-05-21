package com.xunqinli.verifiterm.apli;

import android.app.Application;
import android.content.Context;

import com.xunqinli.verifiterm.utils.AppHook;

public class VerfAplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppHook.get().hookApplicationWatcher(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        AppHook.get().onTerminate(this);

        super.onTerminate();

    }
}
