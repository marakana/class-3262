package com.marakana.android.yamba;

import android.app.Application;

import com.marakana.android.yamba.svc.YambaService;


public class YambaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        YambaService.startPolling(this);

        //System.getProperties().put("http.proxyHost", "proxy.ch.intel.com");
        //System.getProperties().put("http.proxyPort", "911");
    }
}
