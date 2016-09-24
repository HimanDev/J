package com.example.himan.videotest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by himan on 29/8/16.
 */
public class SettingsDatabaseHandler  {

//    private static final int DATABASE_VERSION = 1;
//
//    // Database Name
//    private static final String DATABASE_NAME = "SafeApp";
//
//    // Contacts table name
//    private static final String TABLE_CONTACTS = "settings";
//
//    private static final String KEY_ID = "id";
//    private static final String KEY_MESSAGE = "message";
//    private static final String KEY_MINUTS= "minutes";
//    private static final String KEY_E_MAIL = "email";
//
//
//    public SettingsDatabaseHandler(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT,"
//                + KEY_MINUTS + " INTEGER)";
//        db.execSQL(CREATE_CONTACTS_TABLE);
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//
//        // Create tables again
//        onCreate(db);
//    }
//
//    public void updateSettings(SettingsDto settingsDto) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String count = "SELECT count(*) FROM "+TABLE_CONTACTS;
//        Cursor mcursor = db.rawQuery(count, null);
//        mcursor.moveToFirst();
//        int icount = mcursor.getInt(0);
//        if(icount>0){
//            ContentValues values = new ContentValues();
//            values.put(KEY_MESSAGE, settingsDto.getMessage()); // Contact Name
//            values.put(KEY_MESSAGE, settingsDto.getMinutes()); // Contact Phone Number
//
//            // Inserting Row
//            db.update(TABLE_CONTACTS, values, "_id=" + 1, null);
//            db.close(); // Closing database connection
//
//        }else {
//            ContentValues values = new ContentValues();
//            values.put(KEY_MESSAGE, settingsDto.getMessage()); // Contact Name
//            values.put(KEY_MESSAGE, settingsDto.getMinutes()); // Contact Phone Number
//            // Inserting Row
//            db.insert(TABLE_CONTACTS, null, values);
//            db.close(); // Closing database connection
//
//        }
//
//
//    }
//
//    public SettingsDto getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                        KEY_MESSAGE, KEY_MESSAGE}, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        SettingsDto settingsDto = new SettingsDto(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getInt(2));
//        // return contact
//        return settingsDto;
//    }
}
