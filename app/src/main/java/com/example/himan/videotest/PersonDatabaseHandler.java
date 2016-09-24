package com.example.himan.videotest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himan on 29/7/16.
 */
public class PersonDatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "SafeApp";

    // Contacts table name
    private static final String TABLE_CONTACTS = "person";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone";
    private static final String KEY_E_MAIL = "email";
    private static final String KEY_CODE = "code";

    private static final String TABLE_SETTINGS = "settings";

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_MINUTS= "minutes";


    public PersonDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," +KEY_CODE+" TEXT,"+KEY_E_MAIL+" TEXT)";

        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT,"
                + KEY_MINUTS + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
        Log.v("INFO1", "creating db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);


        // Create tables again
        onCreate(db);
    }
    public void addContact(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, person.getName()); // Contact Name
        values.put(KEY_PH_NO, person.getPhone()); // Contact Phone Number
        values.put(KEY_E_MAIL, person.getEmail()); // Contact Phone Number


        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    public Person getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PH_NO, KEY_CODE, KEY_E_MAIL}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Person person = new Person(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
        // return contact
        return person;
    }

    public List<Person> getAllContacts() {
        List<Person> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Person contact = new Person();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }
    public void deleteContact(Person contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public void updateSettings(SettingsDto settingsDto) {
        SQLiteDatabase db = this.getWritableDatabase();

        String count = "SELECT count(*) FROM "+TABLE_SETTINGS+" ;";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){
            ContentValues values = new ContentValues();
            values.put(KEY_MESSAGE, settingsDto.getMessage()); // Contact Name
            values.put(KEY_MINUTS, settingsDto.getMinutes()); // Contact Phone Number

            // Inserting Row
            db.update(TABLE_SETTINGS, values, KEY_ID+"="+1, null);
            db.close(); // Closing database connection

        }else {
            ContentValues values = new ContentValues();
            values.put(KEY_MESSAGE, settingsDto.getMessage()); // Contact Name
            values.put(KEY_MESSAGE, settingsDto.getMinutes()); // Contact Phone Number
            // Inserting Row
            db.insert(TABLE_SETTINGS, null, values);
            db.close(); // Closing database connection

        }


    }

    public SettingsDto getSettings(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SETTINGS, new String[] { KEY_ID,
                        KEY_MESSAGE, KEY_MESSAGE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SettingsDto settingsDto = new SettingsDto(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getInt(2));
        // return contact
        return settingsDto;
    }

}
