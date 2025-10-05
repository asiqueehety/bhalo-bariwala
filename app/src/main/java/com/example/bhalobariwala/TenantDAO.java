// app/src/main/java/com/example/bhalobariwala/TenantDAO.java
package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bhalobariwala.security.PasswordUtils;

public class TenantDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public TenantDAO(Context ctx) {
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
                DatabaseHelper.T_TENANT,
                new String[]{DatabaseHelper.T_ID},
                DatabaseHelper.T_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public long create(String name, String email, String password, String contact) {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hash(password, salt);

        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.T_NAME, name);
        v.put(DatabaseHelper.T_EMAIL, email);
        v.put(DatabaseHelper.T_CONTACT, contact);
        v.put(DatabaseHelper.T_PWD_HASH, hash);
        v.put(DatabaseHelper.T_SALT, salt);
        v.put(DatabaseHelper.T_CREATED, System.currentTimeMillis());
        // Leave relational columns NULL at signup; link later from app flows:
        // v.put(DatabaseHelper.T_PROP_ID, ...);
        // v.put(DatabaseHelper.T_APT_ID, ...);
        // v.put(DatabaseHelper.T_LID, ...);

        return db.insert(DatabaseHelper.T_TENANT, null, v);
    }

    public boolean validate(String email, String rawPassword) {
        Cursor c = db.query(
                DatabaseHelper.T_TENANT,
                new String[]{DatabaseHelper.T_PWD_HASH, DatabaseHelper.T_SALT},
                DatabaseHelper.T_EMAIL + "=?",
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
}
