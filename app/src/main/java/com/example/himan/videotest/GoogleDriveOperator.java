package com.example.himan.videotest;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DPandey on 23-07-2016.
 */
public class GoogleDriveOperator extends AsyncTask<BlockingQueue<GoogleDriveFileInfo>,Void,Boolean> {

    private Exception exception;
    private GoogleApiClient mGoogleApiClient;
    private DriveId driveId = null;
    private final String TAG="GoogleDriveOperator";
    private static final int REQUEST_CODE_CREATOR = 2;
    MainApp context;
    SharedPreferences sharedPreferences;





    public GoogleDriveOperator(MainApp context, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
        this.sharedPreferences=context.getPreferences(Context.MODE_PRIVATE);
    }


    protected Boolean doInBackground(BlockingQueue<GoogleDriveFileInfo>... queues) {
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
                        // If the operation was not successful, we cannot do
                        // anything
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
                        String title = file.getName();
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
                                    driveFolderResult.getDriveFolder().getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);

                                }
                            });
                        }

                    }
                });
    }


    private  ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult mdRslt) {
                    if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                        String link = mdRslt.getMetadata().getWebContentLink();
                        Log.i(TAG, "");
                    }
                }
            };



}
