package com.codingblocks.VaibhavAlreja.MusicPlayer;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by ITCONTROLLER on 11/29/2017.
 */

public class Multidex extends Application{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
