package com.example.himan.videotest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.himan.videotest.repository.DriveResourceDto;
import com.example.himan.videotest.repository.DriveResourceRepo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.Permission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DPandey on 23-07-2016.
 */
public class GoogleDriveOperator {

    private Exception exception;
    private GoogleApiClient mGoogleApiClient;
    private DriveId driveId = null;
    private String resourceId = null;
    private boolean isResourceShared = false;

    private com.google.api.services.drive.Drive driveService = null;
    private final String TAG="GoogleDriveOperator";
    private static final int REQUEST_CODE_CREATOR = 2;
    Activity context;
    SharedPreferences sharedPreferences;
    static final int REQUEST_AUTHORIZATION = 1001;
    private final static String LINK_APPEND_RESOURCE_ID = "https://drive.google.com/open?id=";


    public GoogleDriveOperator(Activity context, GoogleApiClient mGoogleApiClient, GoogleAccountCredential mCredential) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFERENCE_FILE_KEY_GOOGLE),Context.MODE_PRIVATE);
        initGoogleDriveRestApi(mCredential);
    }

    private void initGoogleDriveRestApi(GoogleAccountCredential mCredential){
        this.driveService = FolderStructure.getInstance().getRestApiDriveService();

    }

    protected Boolean doInBackground(GoogleDriveFileInfo googleDriveFileInfo) {
        try{
            driveService.files().list().execute();
        }
        catch(UserRecoverableAuthIOException e){
            context.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        }
        catch(IOException e){
            Log.i(TAG,e.getMessage());
        }
        try {
            GoogleDriveFileInfo driveFileInfo = googleDriveFileInfo;
            if(!isResourceShared && resourceId != null){
                allowSharePermission();
                isResourceShared = true;
            }
            saveFileToDrive(driveFileInfo);
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    private void saveFileToDrive(final GoogleDriveFileInfo driveFileInfo) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(
                new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        if(driveId == null && driveFileInfo.getExtensionType()!=null) {
                            Log.i(TAG, "Failed DRIVE id");
                            return;
                        }
                        File file = driveFileInfo.getFile();
                        final String title = file.getName();
                        Log.i(TAG, "Connection successful, creating new contents...");
                        // Otherwise, we can write our data to the new contents.
                        // Get an output stream for the contents.


                        if(!driveFileInfo.getFile().isDirectory()) {
                            OutputStream outputStream = result.getDriveContents()
                                    .getOutputStream();
                            FileInputStream fis;
                            try {
                                fis = new FileInputStream(file.getAbsolutePath());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] buf = new byte[(int) 1024];
                                int n;
                                while (-1 != (n = fis.read(buf)))
                                    baos.write(buf, 0, n);
                                byte[] photoBytes = baos.toByteArray();
                                outputStream.write(photoBytes);

                                outputStream.close();
                                outputStream = null;
                                fis.close();
                                fis = null;

                            } catch (FileNotFoundException e) {
                                Log.w(TAG, "FileNotFoundException: " + e.getMessage());
                            } catch (IOException e1) {
                                Log.w(TAG, "Unable to write file contents." + e1.getMessage());
                            }


                            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                    .setTitle(title).build();
                            driveId.asDriveFolder().createFile(mGoogleApiClient,
                                    metadataChangeSet,
                                    result.getDriveContents());

                        }
                        else{
                           driveId = null;
                           String parentDriveIdDecode = sharedPreferences.getString(driveFileInfo.getrFolderTypeKey(), null);
                            DriveId parentDriveId = null;
                            try {
                                parentDriveId = DriveId.decodeFromString(parentDriveIdDecode);
                            }
                            catch(IllegalArgumentException | NullPointerException e){
                                Log.i(TAG, "Exception occured while getting "+driveFileInfo.getrFolderTypeKey()+" drive Id");
                                return;
                            }
                           MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                   .setTitle(title).build();
                           PendingResult result1 = parentDriveId.asDriveFolder().createFolder(mGoogleApiClient,
                                   metadataChangeSet);
                            //result1.await();
                            result1.setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                                    driveId = driveFolderResult.getDriveFolder().getDriveId();
                                    final DriveResourceDto driveResource = new DriveResourceDto( title, driveId.encodeToString(),new GetLocation(context).getLocationUrl());
                                    final DriveResourceRepo dbResource = new DriveResourceRepo();
                                    dbResource.addDriveResource(driveResource);
                                    if(!FolderStructure.getInstance().isNetworkAvailable(context)){
                                        return;
                                    }
                                    driveFolderResult.getDriveFolder().addChangeListener(mGoogleApiClient, new ChangeListener() {
                                        @Override
                                        public void onChange(ChangeEvent event) {
                                            Log.d(TAG, "event: " + event + " resId: " + event.getDriveId().getResourceId());
                                            resourceId = event.getDriveId().getResourceId();
                                            driveResource.setLink(LINK_APPEND_RESOURCE_ID+resourceId);
                                            driveResource.setResourceId(resourceId);
                                            new DriveResourceRepo().updateDriveResource(driveResource);
                                        }
                                    });

                                }
                            });

                        }

                    }
                });

    }

    /**
     * It gives share permission to current folder uploaded or current driveId object set
     * in the driveId reference.
     *
     */
    private void allowSharePermission() {
        if(!FolderStructure.getInstance().isNetworkAvailable(context)){
            return;
        }
        try {
            Permission newPermission = new Permission();
            newPermission.setType("anyone");
            newPermission.setRole("reader");
            Permission p = driveService.permissions().create(resourceId, newPermission).execute();

        }
        catch(UserRecoverableAuthIOException e){
           context.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        }
        catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
        catch (Exception e) {
            Log.i(TAG,e.getMessage());
        }
    }

}
