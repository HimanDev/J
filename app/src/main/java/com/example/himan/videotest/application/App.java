package com.example.himan.videotest.application;

import android.app.Application;
import android.content.Context;

import com.example.himan.videotest.dbhelper.DBHelper;
import com.example.himan.videotest.dbhelper.DatabaseManager;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by himan on 25/9/16.
 */
public class App extends Application {

    private static Context context;
    private static DBHelper dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public static Context getContext(){
        return context;
    }
}
