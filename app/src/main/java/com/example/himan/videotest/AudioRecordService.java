package com.example.himan.videotest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by himan on 5/8/16.
 */
public class AudioRecordService extends Service {

    private static final String TAG = "AudioRecordService";

    private boolean isRunning  = false;
    private MediaRecorder mRecorder = null;
    private static File newAudioFolder;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        isRunning = true;
        newAudioFolder  = FolderStructure.getInstance().getCreateNewVideoFolder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        if(!isRunning){
            new Thread(new Runnable() {
                @Override
                public void run() {


                    mRecorder = new MediaRecorder();
                    startRecording();
                    mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder mr, int what, int extra) {
                            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                stopRecording();
                                stopSelf();
                            }

                        }
                    });
                    mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                        @Override
                        public void onError(MediaRecorder mr, int what, int extra) {
                            stopSelf();
                        }
                    });

                    //Stop service once it finishes its task

                }
            }).start();

            return Service.START_STICKY;

        }

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    private void startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(FolderStructure.getInstance().getAudioLocation(newAudioFolder));
        mRecorder.setMaxDuration(60000);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }


    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();

    }

//    private  String getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                "AUD_"+ timeStamp + ".3gp");
//        return mediaFile.getAbsolutePath();
//    }

}
