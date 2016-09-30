package com.example.himan.videotest;

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
public class GoogleDriveOperator extends AsyncTask<BlockingQueue<GoogleDriveFileInfo>,Void,Boolean> {

    private Exception exception;
    private GoogleApiClient mGoogleApiClient;
    private DriveId driveId = null;
    private String resourceId = null;

    private com.google.api.services.drive.Drive driveService = null;
    private final String TAG="GoogleDriveOperator";
    private static final int REQUEST_CODE_CREATOR = 2;
    Context context;
    SharedPreferences sharedPreferences;


    public GoogleDriveOperator(Context context, GoogleApiClient mGoogleApiClient, GoogleAccountCredential mCredential) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFERENCE_FILE_KEY_GOOGLE),Context.MODE_PRIVATE);
        initGoogleDriveRestApi(mCredential);
    }

    private void initGoogleDriveRestApi(GoogleAccountCredential mCredential){
        this.driveService = new com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), mCredential)
                .setApplicationName("SafeApp")
                .build();
    }

    protected Boolean doInBackground(BlockingQueue<GoogleDriveFileInfo>... queues) {
        try{
            driveService.files().list().execute();
        }
        catch(UserRecoverableAuthIOException e){
//            context.startActivityForResult(e.getIntent(), context.REQUEST_AUTHORIZATION);
        }
        catch(IOException e){
            Log.i(TAG,e.getMessage());
        }
        BlockingQueue<GoogleDriveFileInfo> blockingQueue = queues[0];
        try {
            while (true) {

                GoogleDriveFileInfo driveFileInfo = blockingQueue.take();
                if (driveFileInfo.isApplicationStopped()) {
                    break;
                }
                saveFileToDrive(driveFileInfo);
            }
        }
        catch (InterruptedException e){

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
                                byte[] buf = new byte[(int) file.length()];
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
                            /*Drive.DriveApi.getFolder(mGoogleApiClient,
                                    driveId).createFile(mGoogleApiClient,
                                    metadataChangeSet,
                                    result.getDriveContents());*/
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
//                                    DriveResourceDto driveResource = new DriveResourceDto( title, driveId.encodeToString());
//                                    new DriveResourceRepo().addDriveResource(driveResource);
                                    if(!FolderStructure.getInstance().isNetworkAvailable(context)){
                                        return;
                                    }
                                    driveFolderResult.getDriveFolder().addChangeListener(mGoogleApiClient, new ChangeListener() {
                                        @Override
                                        public void onChange(ChangeEvent event) {
                                            Log.d(TAG, "event: " + event + " resId: " + event.getDriveId().getResourceId());
                                            resourceId = event.getDriveId().getResourceId();
                                        }
                                    });

                                }
                            });

                            try {
                                Thread.sleep(5000);
                                allowSharePermission(title);
                            }
                            catch (InterruptedException e) {
                                Log.i(TAG, e.getMessage());
                            }

                        }

                    }
                });

    }

    private  ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
        ResultCallback<DriveResource.MetadataResult>() {
            @Override
            public void onResult(DriveResource.MetadataResult mdRslt) {
                if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                    String link = mdRslt.getMetadata().getAlternateLink();
                    Log.i(TAG, "ALTERNATE link = " + link);
                    String fileName = mdRslt.getMetadata().getOriginalFilename();
//                    DriveResourceDto driveResource = new DriveResourceDto( fileName, driveId.encodeToString(), resourceId, link);
//                    new DriveResourceRepo().updateDriveResource(driveResource);
                }
            }
        };
    /**
     * It gives share permission to current folder uploaded or current driveId object set
     * in the driveId reference.
     *
     */
    private void allowSharePermission(String title) {
        if(!FolderStructure.getInstance().isNetworkAvailable(context)){
            return;
        }
        try {
            Permission newPermission = new Permission();
            newPermission.setType("anyone");
            newPermission.setRole("reader");
            Permission p = driveService.permissions().create(resourceId, newPermission).execute();
            driveId.asDriveFolder().getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);
        }
        catch(UserRecoverableAuthIOException e){
//            context.startActivityForResult(e.getIntent(), context.REQUEST_AUTHORIZATION);
        }
        catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
        catch (Exception e) {
            Log.i(TAG,e.getMessage());
        }
    }

}
