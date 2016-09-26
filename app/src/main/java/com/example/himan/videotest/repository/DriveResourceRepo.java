package com.example.himan.videotest.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himan.videotest.dbhelper.DatabaseManager;

/**
 * Created by himan on 24/9/16.
 */
public class DriveResourceRepo  {

    private static final int DATABASE_VERSION = 4;

    // Database Name
//    private static final String DATABASE_NAME = "SafeApp";
//
//    // Contacts table name
//    private static final String TABLE_DRIVE_RESOURCE = "drive_resource";
//
//    private static final String KEY_ID = "id";
//    private static final String KEY_FOLDER_NAME = "folder_name";
//    private static final String KEY_DRIVE_ID = "drive_id";
//    private static final String KEY_RESOURCE_ID = "resource_id";
//    private static final String KEY_LINK = "link";


    private DriveResourceDto dbDriveResource;
   public  DriveResourceRepo(){
    dbDriveResource=new DriveResourceDto();
   }

    public static String createTable(){
        return "CREATE TABLE " + DriveResourceDto.TABLE_DRIVE_RESOURCE + "("
                + DriveResourceDto.KEY_ID + " INTEGER PRIMARY KEY," + DriveResourceDto.KEY_FOLDER_NAME + " TEXT,"
                + DriveResourceDto.KEY_DRIVE_ID + " TEXT," + DriveResourceDto.KEY_RESOURCE_ID+" TEXT," + DriveResourceDto.KEY_LINK+" TEXT)";
    }





    public void addDriveResource(DriveResourceDto driveResource) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(DriveResourceDto.KEY_FOLDER_NAME, driveResource.getFolderName()); // Contact Name
        values.put(DriveResourceDto.KEY_DRIVE_ID, driveResource.getDriveId()); // Contact Phone Number
        values.put(DriveResourceDto.KEY_RESOURCE_ID, driveResource.getResourceId()); // Contact Phone Number
        values.put(DriveResourceDto.KEY_LINK, driveResource.getLink()); // Contact Phone Number


        // Inserting Row
        db.insert(DriveResourceDto.TABLE_DRIVE_RESOURCE, null, values);
        DatabaseManager.getInstance().closeDatabase(); // Closing database connection

    }

    public DriveResourceDto getDriveResource(String folderName) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();


        Cursor cursor = db.query(DriveResourceDto.TABLE_DRIVE_RESOURCE, new String[]{DriveResourceDto.KEY_ID,
                        DriveResourceDto.KEY_FOLDER_NAME, DriveResourceDto.KEY_DRIVE_ID, DriveResourceDto.KEY_RESOURCE_ID, DriveResourceDto.KEY_LINK}, DriveResourceDto.KEY_FOLDER_NAME + "=?",
                new String[]{folderName}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();

            DriveResourceDto driveResource = new DriveResourceDto(cursor.getString(cursor.getColumnIndex(DriveResourceDto.KEY_FOLDER_NAME)),cursor.getString(cursor.getColumnIndex(DriveResourceDto.KEY_DRIVE_ID)),
                    cursor.getString(cursor.getColumnIndex(DriveResourceDto.KEY_LINK)),cursor.getString(cursor.getColumnIndex(DriveResourceDto.KEY_RESOURCE_ID)));
            // return contact
            return driveResource;
        }
           return null;
    }
    public void updateDriveResource(DriveResourceDto driveResource) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();


            ContentValues values = new ContentValues();
            values.put(DriveResourceDto.KEY_LINK, driveResource.getLink()); // Contact Name
            values.put(DriveResourceDto.KEY_RESOURCE_ID, driveResource.getResourceId()); // Contact Phone Number
        values.put(DriveResourceDto.KEY_DRIVE_ID,driveResource.getDriveId()); // Contact Phone Number

            // Inserting Row
            db.update(DriveResourceDto.TABLE_DRIVE_RESOURCE, values, DriveResourceDto.KEY_FOLDER_NAME+"="+driveResource.getFolderName(), null);
           DatabaseManager.getInstance().closeDatabase(); // Closing database connection




    }

}
