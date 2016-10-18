package com.example.himan.videotest.dbhelper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.himan.videotest.application.App;
import com.example.himan.videotest.domains.DriveResourceDto;
import com.example.himan.videotest.repository.DriveResourceRepo;
import com.example.himan.videotest.repository.PersonDatabaseRepo;
import com.example.himan.videotest.domains.PersonDto;

/**
 * Created by himan on 25/9/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =11;
    // Database Name
    private static final String DATABASE_NAME = "safeapp.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();



    public DBHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DriveResourceRepo.createTable());
        db.execSQL(PersonDatabaseRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DriveResourceDto.TABLE_DRIVE_RESOURCE);
        db.execSQL("DROP TABLE IF EXISTS " + PersonDto.TABLE_CONTACTS);
        onCreate(db);

    }

}
