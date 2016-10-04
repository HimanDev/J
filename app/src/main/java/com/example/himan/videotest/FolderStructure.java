package com.example.himan.videotest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by himan on 5/9/16.
 */
public class FolderStructure {
    private static final String ROOT_FOLDER_NAME = "MyCameraApp";
    private static final String VIDEO_FOLDER_NAME = "Videos";
    private static final String AUDIO_FOLDER_NAME = "Audios";

    //
    private GoogleApiClient googleApiClient;
    private GoogleAccountCredential googleAccountCredential;

    private com.google.api.services.drive.Drive restApiDriveService = null;


    private BlockingQueue<GoogleDriveFileInfo> queue = new LinkedBlockingQueue<>();

    public File getRootFolder() {
        return rootFolder;
    }

    public File getVideoFolder() {
        return videoFolder;
    }

    public File getAudioFolder() {
        return audioFolder;
    }

    private File rootFolder, videoFolder, audioFolder;

    private static class SingletonInstanceHolder{
        static FolderStructure folderStructure = new FolderStructure();
    }

    private FolderStructure() {
        if (rootFolder != null && videoFolder != null && audioFolder != null) {
            Log.d("MyCameraApp", "Directory Structue created");
        }
        rootFolder = createFolder(Environment.getExternalStorageDirectory(), ROOT_FOLDER_NAME);
        videoFolder = createFolder(rootFolder, VIDEO_FOLDER_NAME);
        audioFolder = createFolder(rootFolder, AUDIO_FOLDER_NAME);
    }

    public static FolderStructure getInstance(){
        return SingletonInstanceHolder.folderStructure;
    }

    private File createFolder(File filePath, String folderPath) {
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (filePath != null) {
            File file = new File(filePath, folderPath);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                }
            }
            return file;
        }
        return null;
    }

    public  File getCreateNewVideoFolder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = createFolder(videoFolder, "VIDEO_" + timeStamp);
        return mediaFile;

    }

    public  String getVideoLocation(File videoFolder) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(videoFolder.getPath() + File.separator +
                "VIDEO_" + timeStamp + ".mp4");
        return mediaFile.getAbsolutePath();

    }

    public  File createNewAudioFolder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = createFolder(audioFolder, "AUDIO_" + timeStamp);
        return mediaFile;

    }

    public  String getAudioLocation(File audioFolder) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(audioFolder.getPath() + File.separator +
                "AUDIO_" + timeStamp + ".3gp");
        return mediaFile.getAbsolutePath();

    }
    public File getRootVideoFolder(){
        return videoFolder;
    }
    public File getRootAudioFolder(){
        return audioFolder;
    }

    /**
     * This method checks whether network/internet connection available.
     *
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public BlockingQueue<GoogleDriveFileInfo> getQueue() {
        return queue;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public GoogleAccountCredential getGoogleAccountCredential() {
        return googleAccountCredential;
    }

    public void setGoogleAccountCredential(GoogleAccountCredential googleAccountCredential) {
        this.googleAccountCredential = googleAccountCredential;
    }

    public Drive getRestApiDriveService() {
        return restApiDriveService;
    }

    public void setRestApiDriveService(Drive restApiDriveService) {
        this.restApiDriveService = restApiDriveService;
    }

}
