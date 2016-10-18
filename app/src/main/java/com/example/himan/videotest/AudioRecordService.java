package com.example.himan.videotest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by himan on 5/8/16.
 */
public class AudioRecordService extends Service {

    public static final String TAG = "AudioRecordService";
    public static final String UPDATE_TIME_EVENT = "com.example.himan.videotest.SosActivity";
    public static final String UI_UPDATE = "com.example.himan.videotest.SosActivity.updateui";

    public static final String TIME_REMAINING="time_remaining";
    public static final String OPTION="option";


    public static boolean isRunning = false;
    private MediaRecorder mRecorder = null;
    private static File newAudioFolder;
    public CountDownTimer countDownTimer;
    private String mNextAudioAbsolutePath;
    private int NUMBER_OF_AUDIO = 0;
    private GoogleDriveOperator googleDriveOperator;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        googleDriveOperator = new GoogleDriveOperator(getApplicationContext(), FolderStructure.getInstance().getGoogleApiClient(), FolderStructure.getInstance().getGoogleAccountCredential());


        newAudioFolder = FolderStructure.getInstance().createNewAudioFolder();
        new Thread(new Runnable() {
            public void run() {
                googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFolderInfoObject(newAudioFolder, getString(R.string.Audio_Folder_Drive_Id)));

            }
        }).start();
    }

    private String formatMilliSecondsToTime(long milliseconds) {

        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)));
//        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
//        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : "
//                + twoDigitString(seconds);
        return  twoDigitString(minutes) + " : "
                + twoDigitString(seconds);
    }

    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        if (!isRunning) {
            isRunning = true;
            countDownTimer = new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG, formatMilliSecondsToTime(millisUntilFinished));
                    Intent intent = new Intent(UPDATE_TIME_EVENT);
                    intent.putExtra(TIME_REMAINING, formatMilliSecondsToTime(millisUntilFinished));
                    sendBroadcast(intent);

                }

                @Override
                public void onFinish() {
                    isRunning=false;
                    Intent intent = new Intent(UI_UPDATE);
                    sendBroadcast(intent);
                    stopSelf();
                }
            }.start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mRecorder = new MediaRecorder();
                    startRecording();
                    mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder mr, int what, int extra) {
                            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "3gp"));

                                    }
                                }).start();
                                stopRecording();

                            }

                        }
                    });
                    mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                        @Override
                        public void onError(MediaRecorder mr, int what, int extra) {
                          //  stopSelf();
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
        new Thread(new Runnable() {
            public void run() {
                googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "3gp"));

            }
        }).start();
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
