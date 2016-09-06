package com.example.himan.videotest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.android.gms.drive.MetadataChangeSet;
/**
 * Created by himan on 28/6/16.
 */
public class MainApp extends Activity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    ImageView imageViewRecordVideo,imageViewRecordAudio,imageViewSOS;
    LinearLayout llMyRecordings;

    //Goolge
    private GoogleApiClient mGoogleApiClient;
    private final String TAG="deepakchutiya";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    public Bitmap mBitmapToSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        imageViewRecordVideo =(ImageView)findViewById(R.id.imageViewRecordVideo);
        imageViewRecordAudio=(ImageView)findViewById(R.id.imageViewRecordAudio);
        imageViewSOS=(ImageView)findViewById(R.id.imageViewSOS);
        llMyRecordings=(LinearLayout)findViewById(R.id.llMyRecordings);
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
        initGoogleDrive();
    }

    private void initGoogleDrive(){
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initGoogleDrive();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
                                                                                 // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
        new GoogleDriveOperator(this,mGoogleApiClient).execute(FolderStructure.getInstance().getQueue());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = getWindow().getDecorView();
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//    }

    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }

}
