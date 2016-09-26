package com.example.himan.videotest.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.himan.videotest.dbhelper.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himan on 29/7/16.
 */
public class PersonDatabaseRepo  {





    public static String createTable() {
        return "CREATE TABLE " + PersonDto.TABLE_CONTACTS + "("
                + PersonDto.KEY_ID + " INTEGER PRIMARY KEY," + PersonDto.KEY_NAME + " TEXT,"
                + PersonDto.KEY_PH_NO + " TEXT," +PersonDto.KEY_CODE+" TEXT,"+PersonDto.KEY_E_MAIL+" TEXT)";


    }


    public void addContact(PersonDto person) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(PersonDto.KEY_NAME, person.getName()); // Contact Name
        values.put(PersonDto.KEY_PH_NO, person.getPhone()); // Contact Phone Number
        values.put(PersonDto.KEY_E_MAIL, person.getEmail()); // Contact Phone Number


        // Inserting Row
        db.insert(PersonDto.TABLE_CONTACTS, null, values);
        DatabaseManager.getInstance().closeDatabase(); // Closing database connection
    }

    public PersonDto getContact(int id) {
        SQLiteDatabase db =DatabaseManager.getInstance().openDatabase();

        Cursor cursor = db.query(PersonDto.TABLE_CONTACTS, new String[]{PersonDto.KEY_ID,
                        PersonDto.KEY_NAME, PersonDto.KEY_PH_NO, PersonDto.KEY_CODE, PersonDto.KEY_E_MAIL}, PersonDto.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PersonDto person = new PersonDto(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
        // return contact
        return person;
    }

    public List<PersonDto> getAllContacts() {
        List<PersonDto> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + PersonDto.TABLE_CONTACTS;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PersonDto contact = new PersonDto();
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
        String countQuery = "SELECT  * FROM " + PersonDto.TABLE_CONTACTS;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }
    public void deleteContact(PersonDto contact) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(PersonDto.TABLE_CONTACTS, PersonDto.KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        DatabaseManager.getInstance().closeDatabase();
    }

}
