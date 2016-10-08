package com.example.himan.videotest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by himan on 5/8/16.
 */
public class AudioRecordService extends Service {

    private static final String TAG = "AudioRecordService";

    private static boolean isRunning  = false;
    private MediaRecorder mRecorder = null;
    private static File newAudioFolder;
    public CountDownTimer countDownTimer;
    private static BlockingQueue<GoogleDriveFileInfo> queue;
    private String mNextAudioAbsolutePath;
    private int NUMBER_OF_AUDIO = 0;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        newAudioFolder  =  FolderStructure.getInstance().createNewAudioFolder();
        queue = FolderStructure.getInstance().getQueue();
        queue.add(GoogleDriveFileInfo.createFolderInfoObject(newAudioFolder, getString(R.string.Audio_Folder_Drive_Id)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        if(!isRunning){
            isRunning = true;
            countDownTimer=new CountDownTimer(30000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG,Long.toString(millisUntilFinished));

                }

                @Override
                public void onFinish() {

                }
            };

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
                                queue.add(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "3gp"));

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
        queue.add(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "mp4"));
        queue.add(GoogleDriveFileInfo.createApplicationStoppedInfoObject());
        Log.i(TAG, "Service onDestroy");
    }

    private void startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mNextAudioAbsolutePath = FolderStructure.getInstance().getAudioLocation(newAudioFolder, ++NUMBER_OF_AUDIO);
        mRecorder.setOutputFile(mNextAudioAbsolutePath);
        mRecorder.setMaxDuration(10000);
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

    }


}
