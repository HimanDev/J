package com.example.himan.videotest;

import java.io.File;

/**
 * Created by DPandey on 05-09-2016.
 */
public class GoogleDriveFileInfo {
    private File file;
    private String extensionType;

    private Operation operationType;
    private String rFolderTypeKey;

    public boolean isApplicationStopped() {
        return applicationStopped;
    }

    private boolean applicationStopped;

    enum Operation{
        DELETE,
        ADD
    };

    public File getFile() {
        return file;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public Operation getOperationType() {
        return operationType;
    }

    private GoogleDriveFileInfo(File file) {
        this.file = file;
        applicationStopped = false;
    }

    public GoogleDriveFileInfo() {
        applicationStopped = true;
    }

    public static GoogleDriveFileInfo createFolderInfoObject(File file, String rFolderTypeKey){
        GoogleDriveFileInfo selfObj = new GoogleDriveFileInfo(file);
        selfObj.setrFolderTypeKey(rFolderTypeKey);
        selfObj.setOperationType(Operation.ADD);
        return selfObj;
    }
    public static GoogleDriveFileInfo createFileInfoObject(File file, String extensionType){
        GoogleDriveFileInfo selfObj = new GoogleDriveFileInfo(file);
        selfObj.setExtensionType(extensionType);
        selfObj.setOperationType(Operation.ADD);
        return selfObj;
    }


    public void setFile(File file) {
        this.file = file;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public void setOperationType(Operation operationType) {
        this.operationType = operationType;
    }

    public String getrFolderTypeKey() {
        return rFolderTypeKey;
    }

    public void setrFolderTypeKey(String rFolderTypeKey) {
        this.rFolderTypeKey = rFolderTypeKey;
    }

    public void setApplicationStopped(boolean applicationStopped) {
        this.applicationStopped = applicationStopped;
    }

}
