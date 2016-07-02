/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.himan.videotest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * This activity uses the camera/camcorder as the A/V source for the {@link MediaRecorder} API.
 * A {@link TextureView} is used as the camera preview which limits the code to API 14+. This
 * can be easily replaced with a {@link android.view.SurfaceView} to run on older devices.
 */
public class MainActivity1 extends Activity implements View.OnClickListener, SurfaceHolder.Callback , MediaRecorder.OnInfoListener {
    private static int MEDIA_TYPE_IMAGE=1;
    private static int MEDIA_TYPE_VIDEO=2;
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    Button btnCapture, btnStop;
    private boolean firstTime = true;
    Handler handler;
    private SurfaceHolder mHolder;

    android.graphics.Camera camera;

    private Camera mCamera;
    private TextureView mTextureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.sample_main);

        recorder = new MediaRecorder();
        initRecorder();
        btnCapture = (Button) findViewById(R.id.button_capture);
        btnStop = (Button) findViewById(R.id.button_stop);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.surface_view);
        mHolder = cameraView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    prepareRecorder();
                    recorder.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void initRecorder() {


        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);





//        CamcorderProfile cpHigh = CamcorderProfile
//                .get(CamcorderProfile.QUALITY_HIGH);
//        recorder.setProfile(cpHigh);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        recorder.setOutputFile(getOutputMediaFile(2));
        recorder.setMaxDuration(10000); // 50 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

    }
    private  String getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
Toast.makeText(MainActivity1.this,mediaFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
        return mediaFile.getAbsolutePath();
    }

//    Timer timer = new Timer();

    private void prepareRecorder() {
        recorder.setPreviewDisplay(mHolder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {

        if (recording) {
            recorder.stop();
            recording = false;

            // Let's initRecorder so we can record again
            initRecorder();
            prepareRecorder();
        } else {
            recording = true;
            recorder.start();
        }
    }

    public void record() {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

//        cameraView.setPreviewCallback(new PreviewCallback() {
//            public void onPreviewFrame(byte[] _data, Camera _camera) {
//                Log.d("onPreviewFrame-surfaceChanged",String.format("Got %d bytes of camera data", _data.length));
//            }
//    }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        mCamera = Camera.open();
//
//        try {
////            mCamera.setPreviewTexture(surface);
////            mCamera.startPreview();
//        } catch (IOException ioe) {
//            // Something bad happened
//        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
            recorder.stop();
        recorder.reset();

        // Flag to recording (in my code I am using to stop/start capturing)
        recording = false;
        recording = true;

        initRecorder();
        prepareRecorder();

        recorder.start();

        Toast.makeText(MainActivity1.this, "Again", Toast.LENGTH_LONG).show();
        // 5 000 ms - 5 s
        // 300 000 ms - 5 min

    }
    }
}