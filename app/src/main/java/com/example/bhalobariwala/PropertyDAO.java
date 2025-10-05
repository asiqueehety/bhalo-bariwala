package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bhalobariwala.model.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * TABLE: property
 * COLS : prop_id (PK AUTOINCREMENT), prop_name, landlord_id (FK -> landlord.id)
 */
public class PropertyDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public PropertyDAO(Context ctx) { helper = new DatabaseHelper(ctx); }

    public void open()  { db = helper.getWritableDatabase(); }
    public void close() { if (db != null && db.isOpen()) db.close(); }

    /** Insert WITHOUT prop_id (auto-increments). */
    public long insert(String name, long landlordId) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.P_NAME, name);
        v.put(DatabaseHelper.P_LANDLORDID, landlordId); // landlord_id
        return db.insert(DatabaseHelper.T_PROPERTY, null, v);
    }

    /** List properties for a specific landlord by landlord_id. */
    public List<Property> listByLandlord(long landlordId) {
        List<Property> list = new ArrayList<>();
        Cursor c = db.query(
                DatabaseHelper.T_PROPERTY,
                new String[]{DatabaseHelper.P_ID, DatabaseHelper.P_NAME, DatabaseHelper.P_LANDLORDID},
                DatabaseHelper.P_LANDLORDID + "=?",
                new String[]{String.valueOf(landlordId)},
                null, null,
                DatabaseHelper.P_ID + " DESC"
        );
        while (c.moveToNext()) {
            list.add(new Property(
                    c.getLong(0),   // prop_id
                    c.getString(1), // prop_name
                    c.getLong(2)    // landlord_id
            ));
        }
        c.close();
        return list;
    }

    // Optional debug helpers
    public int countAll() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.T_PROPERTY, null);
        int cnt = 0; if (c.moveToFirst()) cnt = c.getInt(0);
        c.close(); return cnt;
    }
    public int countByLandlord(long landlordId) {
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.T_PROPERTY +
                        " WHERE " + DatabaseHelper.P_LANDLORDID + "=?",
                new String[]{String.valueOf(landlordId)});
        int cnt = 0; if (c.moveToFirst()) cnt = c.getInt(0);
        c.close(); return cnt;
    }
}
