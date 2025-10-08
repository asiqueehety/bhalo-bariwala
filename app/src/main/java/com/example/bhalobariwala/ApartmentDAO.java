package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.bhalobariwala.model.Apartment;

import java.util.ArrayList;
import java.util.List;

/**
 * TABLE: apartment
 * COLS : apt_id (PK AUTOINCREMENT), a_prop_id (FK -> property.prop_id), a_rent
 */
public class ApartmentDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public ApartmentDAO(Context ctx) { helper = new DatabaseHelper(ctx); }

    public void open()  { db = helper.getWritableDatabase(); }
    public void close() { if (db != null && db.isOpen()) db.close(); }

    /** Insert a new apartment. */
    public long insert(long propertyId, double rent) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.A_PROP_ID, propertyId);
        v.put(DatabaseHelper.A_RENT, rent);
        return db.insert(DatabaseHelper.T_APARTMENT, null, v);
    }

    /** Get available apartments for a property (not assigned to any tenant). */
    public List<Apartment> getAvailableApartmentsByProperty(long propertyId) {
        List<Apartment> list = new ArrayList<>();
        // Select apartments where apt_id is NOT in tenant table's t_apt_id
        String query = "SELECT a." + DatabaseHelper.A_ID + ", a." + DatabaseHelper.A_PROP_ID + ", a." + DatabaseHelper.A_RENT +
                " FROM " + DatabaseHelper.T_APARTMENT + " a " +
                " WHERE a." + DatabaseHelper.A_PROP_ID + "=? " +
                " AND a." + DatabaseHelper.A_ID + " NOT IN " +
                " (SELECT " + DatabaseHelper.T_APT_ID + " FROM " + DatabaseHelper.T_TENANT +
                " WHERE " + DatabaseHelper.T_APT_ID + " IS NOT NULL)";

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(propertyId)});
        while (c.moveToNext()) {
            list.add(new Apartment(
                    c.getLong(0),   // apt_id
                    c.getLong(1),   // a_prop_id
                    c.getDouble(2)  // a_rent
            ));
        }
        c.close();
        return list;
    }

    /** Get apartment by ID. */
    public Apartment getById(long aptId) {
        Cursor c = db.query(
                DatabaseHelper.T_APARTMENT,
                new String[]{DatabaseHelper.A_ID, DatabaseHelper.A_PROP_ID, DatabaseHelper.A_RENT},
                DatabaseHelper.A_ID + "=?",
                new String[]{String.valueOf(aptId)},
                null, null, null
        );
        Apartment apartment = null;
        if (c.moveToFirst()) {
            apartment = new Apartment(
                    c.getLong(0),   // apt_id
                    c.getLong(1),   // a_prop_id
                    c.getDouble(2)  // a_rent
            );
        }
        c.close();
        return apartment;
    }

    /** List all apartments for a property. */
    public List<Apartment> listByProperty(long propertyId) {
        List<Apartment> list = new ArrayList<>();
        Cursor c = db.query(
                DatabaseHelper.T_APARTMENT,
                new String[]{DatabaseHelper.A_ID, DatabaseHelper.A_PROP_ID, DatabaseHelper.A_RENT},
                DatabaseHelper.A_PROP_ID + "=?",
                new String[]{String.valueOf(propertyId)},
                null, null,
                DatabaseHelper.A_ID + " ASC"
        );
        while (c.moveToNext()) {
            list.add(new Apartment(
                    c.getLong(0),   // apt_id
                    c.getLong(1),   // a_prop_id
                    c.getDouble(2)  // a_rent
            ));
        }
        c.close();
        return list;
    }
}

