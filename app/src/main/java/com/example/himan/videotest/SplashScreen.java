package com.example.himan.videotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * Created by himan on 24/10/16.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences=this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), Context.MODE_PRIVATE);


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
