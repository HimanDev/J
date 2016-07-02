package com.example.himan.videotest;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
        import android.media.CamcorderProfile;
        import android.media.MediaRecorder;
        import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.widget.Button;
import android.widget.Toast;

public class MainActivityFinal extends Activity implements SurfaceHolder.Callback{

    Button myButton;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean recording;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recording = false;

        mediaRecorder = new MediaRecorder();
        initMediaRecorder();

        setContentView(R.layout.main);

        SurfaceView myVideoView = (SurfaceView)findViewById(R.id.videoview);
        surfaceHolder = myVideoView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        myButton = (Button)findViewById(R.id.mybutton);
        myButton.setOnClickListener(myButtonOnClickListener);

        if(recording){
            mediaRecorder.stop();
            mediaRecorder.release();
            finish();
        }else{
            mediaRecorder.start();
            recording = true;
            myButton.setText("STOP");
        }
    }

    private Button.OnClickListener myButtonOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if(recording){
                mediaRecorder.stop();
                mediaRecorder.release();
                finish();
            }else{
                mediaRecorder.start();
                recording = true;
                myButton.setText("STOP");
            }
        }};

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        prepareMediaRecorder();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    private void initMediaRecorder(){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setProfile(camcorderProfile_HQ);
        mediaRecorder.setOutputFile(getOutputMediaFile(2));
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Toast.makeText(MainActivityFinal.this,"INFO", Toast.LENGTH_SHORT).show();
                if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    mediaRecorder.stop();
                    mediaRecorder.reset();

                    // Flag to recording (in my code I am using to stop/start capturing)
                    recording = false;
                    recording = true;

                    initMediaRecorder();
                    prepareMediaRecorder();

                    mediaRecorder.start();

                    Toast.makeText(MainActivityFinal.this, "Again", Toast.LENGTH_LONG).show();
                    // 5 000 ms - 5 s
                    // 300 000 ms - 5 min

                }

            }
        });
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Toast.makeText(MainActivityFinal.this,"ERROR", Toast.LENGTH_SHORT).show();

            }
        });
        mediaRecorder.setMaxDuration(5000); // Set max duration 60 sec.
    }

    private static int MEDIA_TYPE_IMAGE=1;
    private static int MEDIA_TYPE_VIDEO=2;
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
        Toast.makeText(MainActivityFinal.this,mediaFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
        return mediaFile.getAbsolutePath();
    }
    private void prepareMediaRecorder(){
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {

        Toast.makeText(MainActivityFinal.this, what+" max "+MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED, Toast.LENGTH_SHORT).show();
        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
            mediaRecorder.stop();
            mediaRecorder.reset();

            // Flag to recording (in my code I am using to stop/start capturing)
            recording = false;
            recording = true;

            initMediaRecorder();
            prepareMediaRecorder();

            mediaRecorder.start();

            Toast.makeText(MainActivityFinal.this, "Again", Toast.LENGTH_LONG).show();
            // 5 000 ms - 5 s
            // 300 000 ms - 5 min

        }
    }
}