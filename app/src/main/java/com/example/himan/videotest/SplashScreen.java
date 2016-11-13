package com.example.himan.videotest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

/**
 * Created by himan on 24/10/16.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_CODE_ACCOUNTS = 1;
    private   int MY_PERMISSIONS_REQUEST_READ_STORAGE ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences=this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), Context.MODE_PRIVATE);
//checkPermission();

        int permissionCheck=ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if(permissionCheck!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    REQUEST_CODE_ACCOUNTS);
        }else {
            splash();
        }



    }

    void splash(){
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(sharedPreferences.getBoolean(getString(R.string.isIntro),false)){
                    startActivity(new Intent(SplashScreen.this,GoogleSignInActivity.class));
                    SplashScreen.this.finish();

                }else {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.isIntro),true);
                    editor.commit();
                    Intent i = new Intent(SplashScreen.this, IntroActivity.class);
                    startActivity(i);
                    SplashScreen.this.finish();
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    splash();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_CODE_ACCOUNTS);

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.GET_ACCOUNTS}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
//            onRequestPermissionsResult(MY_PERMISSIONS_REQUEST_READ_STORAGE,);
        }else {
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    if(sharedPreferences.getBoolean(getString(R.string.isIntro),false)){
                        startActivity(new Intent(SplashScreen.this,GoogleSignInActivity.class));
                        SplashScreen.this.finish();

                    }else {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.isIntro),true);
                        editor.commit();
                        Intent i = new Intent(SplashScreen.this, IntroActivity.class);
                        startActivity(i);
                        SplashScreen.this.finish();
                    }


                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
