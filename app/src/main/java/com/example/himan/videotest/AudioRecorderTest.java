package com.example.himan.videotest;


import android.app.Activity;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;


public class AudioRecorderTest extends Activity
{
    private static final String LOG_TAG = "AudioRecorderTest";

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static File newAudioFolder;
    private String mNextAudioAbsolutePath;
    private int NUMBER_OF_AUDIO = 0;


    private void startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mNextAudioAbsolutePath = FolderStructure.getInstance().getAudioLocation(newAudioFolder, ++NUMBER_OF_AUDIO);
        mRecorder.setOutputFile(mNextAudioAbsolutePath);
        mRecorder.setMaxDuration(5000);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();

    }




    private GoogleDriveOperator googleDriveOperator;
    private Chronometer chronometer;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mRecorder = new MediaRecorder();
       googleDriveOperator= new GoogleDriveOperator(this,FolderStructure.getInstance().getGoogleApiClient(), FolderStructure.getInstance().getGoogleAccountCredential(),false);
        newAudioFolder  = FolderStructure.getInstance().createNewAudioFolder();
        new Thread(new Runnable() {
            public void run() {
                googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFolderInfoObject(newAudioFolder, getString(R.string.Audio_Folder_Drive_Id)));

            }
        }).start();


        setContentView(R.layout.record_audio);
        chronometer=(Chronometer)findViewById(R.id.chronometer);
        startRecording();
        chronometer.start();
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Toast.makeText(AudioRecorderTest.this, "audio saved to "+mNextAudioAbsolutePath, Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        public void run() {
                            googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "3gp"));

                        }
                    }).start();
                    stopRecording();
                    startRecording();
                    // 5 000 ms - 5 s
                    // 300 000 ms - 5 min
                }

            }
        });
        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Toast.makeText(AudioRecorderTest.this, "ERROR", Toast.LENGTH_SHORT).show();

            }
        });
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ivStartStop: {
//                if (mIsRecordingVideo) {
//                    stopRecordingVideo();
//                } else {
//                    startRecordingVideo();
//                }
//                break;
//            }
//            case R.id.ivDark: {
//                float brightness = 1 / (float)255;
//                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//                lp.screenBrightness = 0f;
//                lp.dimAmount=0.0f;
//                lp.alpha=0.00f;
//                getActivity().getWindow().setAttributes(lp);
//            }
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
//        queue.add(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "mp4"));
//        queue.add(GoogleDriveFileInfo.createApplicationStoppedInfoObject());
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        queue.add(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "mp4"));
//        queue.add(GoogleDriveFileInfo.createApplicationStoppedInfoObject());
        new Thread(new Runnable() {
            public void run() {
                googleDriveOperator.doInBackground(GoogleDriveFileInfo.createFileInfoObject(new File(mNextAudioAbsolutePath), "mp4"));

            }
        }).start();
    }
}