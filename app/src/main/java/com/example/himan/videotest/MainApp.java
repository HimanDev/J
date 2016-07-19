package com.example.himan.videotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


/**
 * Created by himan on 28/6/16.
 */
public class MainApp extends Activity {

    ImageView imageViewRecordVideo,imageViewRecordAudio,imageViewSOS;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        imageViewRecordVideo =(ImageView)findViewById(R.id.imageViewRecordVideo);
        imageViewRecordAudio=(ImageView)findViewById(R.id.imageViewRecordAudio);
        imageViewSOS=(ImageView)findViewById(R.id.imageViewSOS);
        imageViewRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                float brightness = 1 / (float)255;
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.screenBrightness = 0f;
//                lp.dimAmount=0.0f;
//                lp.alpha=0.00f;
//                getWindow().setAttributes(lp);

                Intent intent = new Intent(MainApp.this, CameraActivity.class);
                startActivity(intent);

            }
        });
        imageViewRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainApp.this, AudioRecorderTest.class);
                startActivity(intent);

            }
        });
        imageViewSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainApp.this, SOSActivity.class);
                startActivity(intent);
            }
        });


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
