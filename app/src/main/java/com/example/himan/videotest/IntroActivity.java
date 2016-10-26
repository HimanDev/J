package com.example.himan.videotest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by himan on 24/10/16.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("Safe in cloud", "When you record an audio or video, we start uploading it to your Google Drive while you're recording it. So, even if someone takes away your phone, most of the recording is already backed up to the cloud.", R.drawable.intro_cloud, getResources().getColor(R.color.splash_background)));
        addSlide(AppIntroFragment.newInstance("Location tracking", "When you record audio or video, we keep a track of your location and save it with the recording. This enables you to have evidence of where the video was recorded. This also comes in handy when you send an SOS.", R.drawable.intro_location, getResources().getColor(R.color.splash_background)));
        addSlide(AppIntroFragment.newInstance("Sos", "If you're in danger, you can reach out to your people so that they can help you. When you send out an SOS, your people are alerted with your message, your realtime location and the realtime audio that's being recorded from your phone. You can customise how much you want to share with SOS message in settings.", R.drawable.sos_intro, getResources().getColor(R.color.splash_background)));

    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, GoogleSignInActivity.class));
        this.finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
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
