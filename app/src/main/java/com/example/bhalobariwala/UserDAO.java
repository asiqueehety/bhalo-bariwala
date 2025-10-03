package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insert
    public long insertUser(String name, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NAME, name);
        values.put(DatabaseHelper.COL_EMAIL, email);
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    // Fetch All
    public Cursor getAllUsers() {
        return db.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null);
    }
}

