package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public long create(String name, String email, String password, String contact, String propId, String aptId) {
        try {
            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hash(password, salt);

            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.T_NAME, name);
            v.put(DatabaseHelper.T_EMAIL, email);
            v.put(DatabaseHelper.T_CONTACT, contact);
            v.put(DatabaseHelper.T_PWD_HASH, hash);
            v.put(DatabaseHelper.T_SALT, salt);
            v.put(DatabaseHelper.T_CREATED, System.currentTimeMillis());
            v.put(DatabaseHelper.T_PROP_ID, propId);
            v.put(DatabaseHelper.T_APT_ID, aptId);

            long result = db.insert(DatabaseHelper.T_TENANT, null, v);
            if (result == -1) {
                Log.e("TenantDAO", "Insert failed for tenant: " + email);
            }
            return result;
        } catch (Exception e) {
            Log.e("TenantDAO", "Exception during tenant creation", e);
            return -1;
        }
    }

    public int validate(String email, String rawPassword) {
        Cursor c = db.query(
                DatabaseHelper.T_TENANT,
                new String[]{DatabaseHelper.T_ID, DatabaseHelper.T_PWD_HASH, DatabaseHelper.T_SALT},
                DatabaseHelper.T_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        int id = -1;
        if (c.moveToFirst()) {
            String storedHash = c.getString(1);
            String salt = c.getString(2);
            if (PasswordUtils.hash(rawPassword, salt).equals(storedHash)) {
                id = c.getInt(0); // tenant ID
            }
        }
        c.close();
        return id;
    }

    /** NEW: Return the tenant's primary key by email, or -1 if not found */
    public long getIdByEmail(String email) {
        long id = -1L;
        Cursor c = db.query(
                DatabaseHelper.T_TENANT,
                new String[]{DatabaseHelper.T_ID},
                DatabaseHelper.T_EMAIL + "=?",
                new String[]{email},
                null, null, null,
                "1"
        );
        if (c.moveToFirst()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }

    /** OPTIONAL: If you ever need the full row for a tenant by email */
    public Cursor getByEmail(String email) {
        return db.query(
                DatabaseHelper.T_TENANT,
                null,
                DatabaseHelper.T_EMAIL + "=?",
                new String[]{email},
                null, null, null,
                "1"
        );
    }

    /** Get tenant by ID */
    public Cursor getTenantById(long tenantId) {
        return db.query(
                DatabaseHelper.T_TENANT,
                null,
                DatabaseHelper.T_ID + "=?",
                new String[]{String.valueOf(tenantId)},
                null, null, null,
                "1"
        );
    }

    /** Update tenant information (except password) */
    public boolean updateTenant(long tenantId, String name, String contact, String propId, String aptId) {
        try {
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.T_NAME, name);
            v.put(DatabaseHelper.T_CONTACT, contact);
            v.put(DatabaseHelper.T_PROP_ID, propId);
            v.put(DatabaseHelper.T_APT_ID, aptId);

            int rowsAffected = db.update(
                    DatabaseHelper.T_TENANT,
                    v,
                    DatabaseHelper.T_ID + "=?",
                    new String[]{String.valueOf(tenantId)}
            );

            if (rowsAffected > 0) {
                Log.d("TenantDAO", "Tenant updated successfully: " + tenantId);
                return true;
            } else {
                Log.e("TenantDAO", "Update failed for tenant: " + tenantId);
                return false;
            }
        } catch (Exception e) {
            Log.e("TenantDAO", "Exception during tenant update", e);
            return false;
        }
    }

    /** Update tenant password */
    public boolean updatePassword(long tenantId, String oldPassword, String newPassword) {
        try {
            // First verify old password
            Cursor c = db.query(
                    DatabaseHelper.T_TENANT,
                    new String[]{DatabaseHelper.T_PWD_HASH, DatabaseHelper.T_SALT},
                    DatabaseHelper.T_ID + "=?",
                    new String[]{String.valueOf(tenantId)},
                    null, null, null
            );

            if (!c.moveToFirst()) {
                c.close();
                return false;
            }

            String storedHash = c.getString(0);
            String salt = c.getString(1);
            c.close();

            // Verify old password
            if (!PasswordUtils.hash(oldPassword, salt).equals(storedHash)) {
                Log.e("TenantDAO", "Old password verification failed");
                return false;
            }

            // Generate new hash with new salt
            String newSalt = PasswordUtils.generateSalt();
            String newHash = PasswordUtils.hash(newPassword, newSalt);

            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.T_PWD_HASH, newHash);
            v.put(DatabaseHelper.T_SALT, newSalt);

            int rowsAffected = db.update(
                    DatabaseHelper.T_TENANT,
                    v,
                    DatabaseHelper.T_ID + "=?",
                    new String[]{String.valueOf(tenantId)}
            );

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("TenantDAO", "Exception during password update", e);
            return false;
        }
    }
}
