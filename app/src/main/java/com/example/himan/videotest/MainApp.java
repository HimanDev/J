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

    ImageView imageViewRecordAudio;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        imageViewRecordAudio=(ImageView)findViewById(R.id.imageViewRecordVideo);
        imageViewRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApp.this,MainActivityFinal.class);
                startActivity(intent);

            }
        });


    }
}
