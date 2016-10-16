package com.example.himan.videotest;

import android.Manifest;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;

import java.io.IOException;
import java.util.Arrays;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;




/**
 * Created by himan on 28/6/16.
 */
public class MainApp extends Activity {

    private   int MY_PERMISSIONS_REQUEST_READ_STORAGE ;
    ImageView imageViewRecordVideo,imageViewRecordAudio,imageViewSOS;
    LinearLayout donateMoney;
    LinearLayout llMyRecordings;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private final String TAG=MainApp.class.getSimpleName();
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        checkPermission();
        sharedPreferences=this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_KEY_GOOGLE),Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();


        imageViewRecordVideo =(ImageView)findViewById(R.id.imageViewRecordVideo);
        imageViewRecordAudio=(ImageView)findViewById(R.id.imageViewRecordAudio);
        imageViewSOS=(ImageView)findViewById(R.id.imageViewSOS);
        llMyRecordings=(LinearLayout)findViewById(R.id.llMyRecordings);
        donateMoney = (LinearLayout)findViewById(R.id.donateMoney);

        donateMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainApp.this, PaymentActivity.class);
                startActivity(intent);
            }
            });

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
                Intent intent = new Intent(MainApp.this, SosActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left, R.anim.push_right);

            }
        });
        llMyRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainApp.this, MyRecordings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left, R.anim.push_right);

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(sharedPreferences==null){
            sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
            editor=sharedPreferences.edit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==REQUEST_CODE_CAPTURE_IMAGE){
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * @see interface definition
     */
    @Override
    public void onStop(){
        super.onStop();
    }


    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.GET_ACCOUNTS},MY_PERMISSIONS_REQUEST_READ_STORAGE);
        }
    }

}
