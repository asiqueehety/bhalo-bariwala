package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bhalobariwala.security.PasswordUtils;

public class UserDAO {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() { db = dbHelper.getWritableDatabase(); }
    public void close() { if (db != null && db.isOpen()) db.close(); }

    public boolean emailExists(String email) {
        Cursor c = db.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COL_ID},
                DatabaseHelper.COL_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // UPDATED: accepts buildingId parameter
    public long createUser(String name, String email, String rawPassword, String role, String buildingId) {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hash(rawPassword, salt);

        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_NAME, name);
        v.put(DatabaseHelper.COL_EMAIL, email);
        v.put(DatabaseHelper.COL_PWD_HASH, hash);
        v.put(DatabaseHelper.COL_SALT, salt);
        v.put(DatabaseHelper.COL_ROLE, role);
        v.put(DatabaseHelper.COL_BUILDING_ID, buildingId);
        v.put(DatabaseHelper.COL_CREATED, System.currentTimeMillis());

        return db.insert(DatabaseHelper.TABLE_USERS, null, v);
    }

    public boolean validateLogin(String email, String rawPassword, String expectedRole) {
        Cursor c = db.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COL_PWD_HASH, DatabaseHelper.COL_SALT, DatabaseHelper.COL_ROLE},
                DatabaseHelper.COL_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean ok = false;
        if (c.moveToFirst()) {
            String storedHash = c.getString(0);
            String salt       = c.getString(1);
            String role       = c.getString(2);
            String computed   = PasswordUtils.hash(rawPassword, salt);
            ok = storedHash.equals(computed) && role.equalsIgnoreCase(expectedRole);
        }
        c.close();
        return ok;
    }
}
