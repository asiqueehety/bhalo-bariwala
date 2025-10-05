// app/src/main/java/com/example/bhalobariwala/LandlordDAO.java
package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bhalobariwala.security.PasswordUtils;

public class LandlordDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public LandlordDAO(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public boolean emailExists(String email) {
        Cursor c = db.query(
                DatabaseHelper.T_LANDLORD,
                new String[]{DatabaseHelper.L_ID},
                DatabaseHelper.L_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    public long create(String name, String email, String password, String contact) {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hash(password, salt);

        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.L_NAME, name);
        v.put(DatabaseHelper.L_EMAIL, email);
        v.put(DatabaseHelper.L_CONTACT, contact);
        v.put(DatabaseHelper.L_PWD_HASH, hash);
        v.put(DatabaseHelper.L_SALT, salt);
        v.put(DatabaseHelper.L_CREATED, System.currentTimeMillis());

        return db.insert(DatabaseHelper.T_LANDLORD, null, v);
    }

    public int validate(String email, String rawPassword) {
        Cursor c = db.query(
                DatabaseHelper.T_LANDLORD,
                new String[]{DatabaseHelper.L_ID, DatabaseHelper.L_PWD_HASH, DatabaseHelper.L_SALT},
                DatabaseHelper.L_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        int id = -1;
        if (c.moveToFirst()) {
            String storedHash = c.getString(1);
            String salt = c.getString(2);
            if (PasswordUtils.hash(rawPassword, salt).equals(storedHash)) {
                id = c.getInt(0); // landlord ID
            }
        }
        c.close();
        return id;
    }

}
