package com.example.himan.videotest;

import java.io.File;

/**
 * Created by DPandey on 05-09-2016.
 */
public class GoogleDriveFileInfo {
    private File file;
    private String extensionType;
    private Operation operationType;

    public boolean isApplicationStopped() {
        return applicationStopped;
    }

    private boolean applicationStopped;

    public File getFile() {
        return file;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public Operation getOperationType() {
        return operationType;
    }

    public GoogleDriveFileInfo(File file, String extensionType, Operation operationType) {
        this.file = file;
        this.extensionType = extensionType;
        this.operationType = operationType;
        applicationStopped = false;
    }
    public GoogleDriveFileInfo() {
        applicationStopped = true;
    }
    enum Operation{
        DELETE,
        ADD
    };
}
