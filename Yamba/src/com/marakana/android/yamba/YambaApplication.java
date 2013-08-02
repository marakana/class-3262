package com.marakana.android.yamba;

import android.app.Application;


public class YambaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //System.getProperties().put("http.proxyHost", "proxy.ch.intel.com");
        //System.getProperties().put("http.proxyPort", "911");
    }
}
