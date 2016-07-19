package com.example.himan.videotest;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.hardware.camera2.CameraManager;

public class MainActivityFinal extends Activity implements SurfaceHolder.Callback{

    Button myButton;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean recording;
    ImageView imageViewRevert;
    boolean backcam=true;
    SurfaceView myVideoView;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
imageViewRevert=(ImageView)findViewById(R.id.revert);
        recording = false;

        mediaRecorder = new MediaRecorder();
        initMediaRecorder();


         myVideoView = (SurfaceView)findViewById(R.id.videoview);

        surfaceHolder = myVideoView.getHolder();
        surfaceHolder.addCallback(this);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
             surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        myButton = (Button)findViewById(R.id.mybutton);
        myButton.setOnClickListener(myButtonOnClickListener);
        imageViewRevert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backcam=!backcam;
                Toast.makeText(MainActivityFinal.this,"backcam "+backcam,Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
//        mediaRecorder.start();
//        recording = true;
//        myButton.setText("STOP");
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
        setupCamera(arg0.getSurfaceFrame().width(),arg0.getSurfaceFrame().height());

        prepareMediaRecorder();
        mediaRecorder.start();
        recording = true;
        myButton.setText("STOP");
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (recording) {
            mediaRecorder.stop();
            recording = false;
        }
        mediaRecorder.release();
        finish();

    }
    private void frontCameraId(){

    }
    private Integer mSensorOrientation;
    private Size mPreviewSize;
    String cameraId;

    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;
    private String mCameraId;

    private void setupCamera(int viewWidth, int viewHeight) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotatedWidth = viewWidth;
                int rotatedHeight = viewHeight;
                if(swapRotation) {
                    rotatedWidth = viewHeight;
                    rotatedHeight = viewWidth;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
//        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
//            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private static int sensorToDeviceRotation(CameraCharacteristics c, int deviceOrientation) {
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);
        // Get device orientation in degrees
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    private int mTotalRotation;

    private void initMediaRecorder(){
//        int sensorOrientation=0;
//        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try{

//             cameraId = manager.getCameraIdList()[0];
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//             sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//
//
//
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
//            mTotalRotation = sensorToDeviceRotation(characteristics, deviceOrientation);
//            boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
//            int rotatedWidth = myVideoView.getWidth();
//            int rotatedHeight = myVideoView.getHeight();
//            if(swapRotation) {
//                rotatedWidth =  myVideoView.getHeight();
//                rotatedHeight =  myVideoView.getWidth();
//            }
//            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceHolder.class), rotatedWidth, rotatedHeight);
//            mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);



//        boolean hp=CamcorderProfile.hasProfile(1, CamcorderProfile.QUALITY_480P);
//        Toast.makeText(MainActivityFinal.this, "HsaP"+hp, Toast.LENGTH_LONG).show();
//
//        StreamConfigurationMap map = characteristics
//                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//        mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//        mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
//        mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceView.class),
//                myVideoView.getWidth(), myVideoView.getHeight(), mVideoSize);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceHolder.setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        } else {
            surfaceHolder.setFixedSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
        }
        }catch (Exception e){
            e.printStackTrace();
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (backcam) {
            mediaRecorder.setProfile(CamcorderProfile
                    .get(0,CamcorderProfile.QUALITY_HIGH));

            mediaRecorder.setOrientationHint(mTotalRotation);

        } else {

            mediaRecorder.setProfile(CamcorderProfile
                    .get(1,CamcorderProfile.QUALITY_480P));
            mediaRecorder.setOrientationHint(mTotalRotation);
        }
//        CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(0,CamcorderProfile.QUALITY_LOW);
//        mediaRecorder.setProfile(camcorderProfile_HQ);
        mediaRecorder.setOutputFile(getOutputMediaFile(2));
        mediaRecorder.setMaxDuration(5000); // Set max duration 60 sec.
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Toast.makeText(MainActivityFinal.this, "INFO", Toast.LENGTH_SHORT).show();
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
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
                Toast.makeText(MainActivityFinal.this, "ERROR", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            //Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
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

//    public void onInfo(MediaRecorder mr, int what, int extra) {
//
//        Toast.makeText(MainActivityFinal.this, what+" max "+MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED, Toast.LENGTH_SHORT).show();
//        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
//            mediaRecorder.stop();
//            mediaRecorder.reset();
//
//            // Flag to recording (in my code I am using to stop/start capturing)
//            recording = false;
//            recording = true;
//
//            initMediaRecorder();
//            prepareMediaRecorder();
//
//            mediaRecorder.start();
//
//            Toast.makeText(MainActivityFinal.this, "Again", Toast.LENGTH_LONG).show();
//            // 5 000 ms - 5 s
//            // 300 000 ms - 5 min
//
//        }
//    }


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
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}