package com.example.himan.videotest;


import android.app.Activity;
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


public class AudioRecorderTest extends Activity
{
    private static final String LOG_TAG = "AudioRecorderTest";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    private  String getOutputMediaFile(){
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
           mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AUD_"+ timeStamp + ".3gp");
        Toast.makeText(AudioRecorderTest.this,mediaFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        return mediaFile.getAbsolutePath();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }

//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }

//    private void stopPlaying() {
//        mPlayer.release();
//        mPlayer = null;
//    }

    private void startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getOutputMediaFile());
        mRecorder.setMaxDuration(60000);
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
        mRecorder.release();

    }

//    class RecordButton extends Button {
//        boolean mStartRecording = true;
//
//        OnClickListener clicker = new OnClickListener() {
//            public void onClick(View v) {
//                onRecord(mStartRecording);
//                if (mStartRecording) {
//                    setText("Stop recording");
//                } else {
//                    setText("Start recording");
//                }
//                mStartRecording = !mStartRecording;
//            }
//        };
//
//        public RecordButton(Context ctx) {
//            super(ctx);
//            setText("Start recording");
//            setOnClickListener(clicker);
//        }
//    }

//    class PlayButton extends Button {
//        boolean mStartPlaying = true;
//
//        OnClickListener clicker = new OnClickListener() {
//            public void onClick(View v) {
//                onPlay(mStartPlaying);
//                if (mStartPlaying) {
//                    setText("Stop playing");
//                } else {
//                    setText("Start playing");
//                }
//                mStartPlaying = !mStartPlaying;
//            }
//        };
//
//        public PlayButton(Context ctx) {
//            super(ctx);
//            setText("Start playing");
//            setOnClickListener(clicker);
//        }
//    }

//    public AudioRecorderTest() {
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/AudioRecorderTest.3gp";
//    }



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mRecorder = new MediaRecorder();
        setContentView(R.layout.record_audio);
        startRecording();
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
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

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}