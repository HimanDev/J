package com.example.himan.videotest.repository;

/**
 * Created by himan on 24/9/16.
 */
public class DriveResourceDto {

    public static final String TAG = DriveResourceDto.class.getSimpleName();

    public static final String TABLE_DRIVE_RESOURCE = "drive_resource";

    public static final String KEY_ID = "id";
    public static final String KEY_FOLDER_NAME = "folder_name";
    public static final String KEY_DRIVE_ID = "drive_id";
    public static final String KEY_RESOURCE_ID = "resource_id";
    public static final String KEY_LINK = "link";



    private int id;
    private String folderName;
    private String driveId;

    public DriveResourceDto() {

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;

    public DriveResourceDto(String folderName, String driveId, String link, String resourceId) {
        this.folderName = folderName;
        this.driveId = driveId;
        this.link = link;
        this.resourceId = resourceId;
    }

    public DriveResourceDto(String folderName, String driveId) {
        this(folderName, driveId, null, null);
    }


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String resourceId;

    @Override
    public String toString() {
        return "DriveResourceDto{" +
                "id=" + id +
                ", folderName='" + folderName + '\'' +
                ", driveId='" + driveId + '\'' +
                ", link='" + link + '\'' +
                ", resourceId='" + resourceId + '\'' +
                '}';
    }
}
