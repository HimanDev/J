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
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;

import java.util.Arrays;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;




/**
 * Created by himan on 28/6/16.
 */
public class MainApp extends Activity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private   int MY_PERMISSIONS_REQUEST_READ_STORAGE ;
    ImageView imageViewRecordVideo,imageViewRecordAudio,imageViewSOS;
    LinearLayout llMyRecordings;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Goolge
    GoogleAccountCredential mCredential = null;
    private GoogleApiClient mGoogleApiClient;
    private final String TAG=MainApp.class.getSimpleName();
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private boolean initialised = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        checkPermission();
        sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();


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

        //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initGoogleDrive(){
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addApi(Plus.API)
                    .addScope(Drive.SCOPE_FILE).addScope(Plus.SCOPE_PLUS_PROFILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initGoogleDrive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGoogleDrive();
    }

    /**
     * @see interface definition
     */
    @Override
    public void onStop(){
        super.onStop();
        FolderStructure.getInstance().getQueue().add(GoogleDriveFileInfo.createApplicationStoppedInfoObject());
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
            mGoogleApiClient.connect();
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if(initialised){
            return;
        }
        Log.i(TAG, "API client connected.");

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(DriveScopes.DRIVE_METADATA, DriveScopes.DRIVE))
                .setBackOff(new ExponentialBackOff());
        getProfileInformation();
        createGDriveFolder();
        new GoogleDriveOperator(this,mGoogleApiClient, mCredential).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FolderStructure.getInstance().getQueue());

        FolderStructure.getInstance().setGoogleApiClient(mGoogleApiClient);
        FolderStructure.getInstance().setGoogleAccountCredential(mCredential);
        initialised = true;
    }

    /**
     *
     *
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                com.google.android.gms.plus.model.people.Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                //below commented details not required as of now.
                //String personName = currentPerson.getDisplayName();
                //String personPhotoUrl = currentPerson.getImage().getUrl();
                //String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                if (mCredential.getSelectedAccountName() == null) {
                    mCredential.setSelectedAccountName(email);
                }
                Log.i(TAG, "email Name" + email);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void createGDriveFolder() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating initial Folders on google drive.");
        String driveIdKey = sharedPreferences.getString(getString(R.string.App_Folder_Drive_Id),null);
        try {
            if(driveIdKey != null) {
                DriveId driveId = DriveId.decodeFromString(driveIdKey);
                if (driveId != null) {
                    createGDriveSubFolder(driveId, getString(R.string.Videos_Folder_Name), getString(R.string.Video_Folder_Drive_Id), true);
                    createGDriveSubFolder(driveId, getString(R.string.Audios_Folder_Name), getString(R.string.Audio_Folder_Drive_Id), true);
                    return;
                }
            }
        }


        catch(IllegalArgumentException | NullPointerException e){
            Log.e(TAG, "Initial Folders on google drive does not exist/deleted.",e);
        }

        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle(getString(R.string.App_Folder_Name)).build();

        PendingResult result = Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient,
                metadataChangeSet);
        result.setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                DriveId driveId = driveFolderResult.getDriveFolder().getDriveId();
                editor.putString(getString(R.string.App_Folder_Drive_Id),driveId.encodeToString());
                editor.commit();
                createGDriveSubFolder(driveId, getString(R.string.Videos_Folder_Name), getString(R.string.Video_Folder_Drive_Id), false);
                createGDriveSubFolder(driveId, getString(R.string.Audios_Folder_Name), getString(R.string.Audio_Folder_Drive_Id), false);
            }
        });

    }

    private void createGDriveSubFolder(DriveId parentDriveId, String title, final String  rDriveIdKey, boolean canExist) {

        if(canExist){
            String driveIdKey = sharedPreferences.getString(rDriveIdKey,null);
            try {
                DriveId driveId = DriveId.decodeFromString(driveIdKey);
                if(driveId != null){
                    return;
                }
            }
            catch(IllegalArgumentException e){
                Log.e(TAG, "Initial"+title+" Folder on google drive does not exist/deleted.");
            }
            return;
        }
        Log.i(TAG, "Creating initial Sub Folders on google drive.");
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle(title).build();
        PendingResult result = parentDriveId.asDriveFolder().createFolder(mGoogleApiClient,
                metadataChangeSet);
        result.setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                DriveId driveId = driveFolderResult.getDriveFolder().getDriveId();
                editor.putString(rDriveIdKey, driveId.encodeToString());
                editor.commit();
            }
        });
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
