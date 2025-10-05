// app/src/main/java/com/example/bhalobariwala/LandlordDAO.java
package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bhalobariwala.security.PasswordUtils;
import com.example.bhalobariwala.ui.owner.OwnerProfileActivity;

public class LandlordDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public LandlordDAO(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    // Open DB
    public void open() {
        db = helper.getWritableDatabase();
    }

    // Close DB
    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    // Check if email already exists
    public boolean emailExists(String email) {
        Cursor c = db.query(
                DatabaseHelper.T_LANDLORD,
                new String[]{DatabaseHelper.L_ID},
                DatabaseHelper.L_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // Create new landlord
    public long create(String name, String email, String password, String contact) {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hash(password, salt);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.L_NAME, name);
        values.put(DatabaseHelper.L_EMAIL, email);
        values.put(DatabaseHelper.L_CONTACT, contact);
        values.put(DatabaseHelper.L_PWD_HASH, hash);
        values.put(DatabaseHelper.L_SALT, salt);
        values.put(DatabaseHelper.L_CREATED, System.currentTimeMillis());

        return db.insert(DatabaseHelper.T_LANDLORD, null, values);
    }

    // Validate login credentials
    public boolean validate(String email, String rawPassword) {
        Cursor c = db.query(
                DatabaseHelper.T_LANDLORD,
                new String[]{DatabaseHelper.L_PWD_HASH, DatabaseHelper.L_SALT},
                DatabaseHelper.L_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        boolean ok = false;
        if (c.moveToFirst()) {
            String storedHash = c.getString(0);
            String salt = c.getString(1);
            ok = PasswordUtils.hash(rawPassword, salt).equals(storedHash);
        }
        c.close();
        return ok;
    }

    // Fetch landlord by ID
    public OwnerProfileActivity.Landlord getLandlordById(long id) {
        Cursor c = db.query(
                DatabaseHelper.T_LANDLORD,
                new String[]{DatabaseHelper.L_ID, DatabaseHelper.L_NAME, DatabaseHelper.L_EMAIL, DatabaseHelper.L_CONTACT},
                DatabaseHelper.L_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        OwnerProfileActivity.Landlord landlord = null;
        if (c.moveToFirst()) {
            landlord = new OwnerProfileActivity.Landlord(
                    c.getLong(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)
            );
        }
        c.close();
        return landlord;
    }

    // Update landlord profile info
    public boolean updateLandlord(long id, String name, String email, String contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.L_NAME, name);
        values.put(DatabaseHelper.L_EMAIL, email);
        values.put(DatabaseHelper.L_CONTACT, contact);

        int rows = db.update(DatabaseHelper.T_LANDLORD, values, DatabaseHelper.L_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}
