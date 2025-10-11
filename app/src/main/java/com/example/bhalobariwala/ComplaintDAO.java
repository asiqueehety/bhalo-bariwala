package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComplaintDAO
 * Adds complaints and lists complaints for ALL tenants
 * who live in the SAME property as the current tenant.
 *
 * Adjust the table/column constants below if your schema differs.
 */
public class ComplaintDAO {

    // ----- SCHEMA CONSTANTS (EDIT IF NEEDED) -----
    // tenant table
    private static final String T_TENANT   = "tenant";
    private static final String T_ID       = "t_id";
    private static final String T_PROP_ID  = "t_prop_id";
    private static final String T_APT_ID   = "t_apartment_id";

    // complaints table
    private static final String T_COMPLAINTS = "complaints";
    private static final String C_ID         = "c_id";
    private static final String C_TITLE      = "c_title";
    private static final String C_DESC       = "c_desc";
    private static final String C_TID        = "c_tid";   // FK → tenant.t_id
    private static final String C_TYPE       = "c_type";  // e.g., electricity/gas/water/security/maintenance
    // ----------------------------------------------

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public ComplaintDAO(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public void open() {
        if (db == null || !db.isOpen()) db = helper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    /** Insert a new complaint for the given tenant. Returns rowId (>0) or -1 on failure. */
    public long addComplaint(long tenantId, String title, String desc, String type) {
        open();
        ContentValues v = new ContentValues();
        v.put(C_TITLE, safe(title));
        v.put(C_DESC, desc == null ? "" : desc.trim());
        v.put(C_TID, tenantId);
        v.put(C_TYPE, safe(type));
        return db.insert(T_COMPLAINTS, null, v);
    }

    /**
     * List ALL complaints for the SAME PROPERTY as the given tenant.
     * We infer property via the tenant’s t_prop_id (no schema change).
     *
     * Each map contains: title, desc, type, prop_id, apt_id
     */
    public List<Map<String, String>> getComplaintsForBuilding(long tenantId) {
        open();

        final String sql =
                "SELECT c." + C_TITLE + ", " +            // 0
                        "c." + C_DESC  + ", " +            // 1
                        "c." + C_TYPE  + ", " +            // 2
                        "t." + T_APT_ID + ", " +           // 3
                        "t." + T_PROP_ID + " " +           // 4
                        "FROM " + T_COMPLAINTS + " c " +
                        "JOIN " + T_TENANT + " t " +
                        "  ON c." + C_TID + " = t." + T_ID + " " +
                        "WHERE t." + T_PROP_ID + " = (" +
                        "   SELECT " + T_PROP_ID +
                        "   FROM " + T_TENANT +
                        "   WHERE " + T_ID + " = ?" +
                        ") " +
                        "ORDER BY c." + C_ID + " DESC";

        Cursor cur = db.rawQuery(sql, new String[]{ String.valueOf(tenantId) });

        List<Map<String, String>> list = new ArrayList<>();
        try {
            while (cur.moveToNext()) {
                Map<String, String> m = new HashMap<>();
                m.put("title",   nz(cur.getString(0)));
                m.put("desc",    nz(cur.getString(1)));
                m.put("type",    nz(cur.getString(2)));
                m.put("apt_id",  nz(cur.getString(3)));
                m.put("prop_id", nz(cur.getString(4)));
                list.add(m);
            }
        } finally {
            cur.close();
        }
        return list;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String nz(String s)   { return s == null ? "" : s; }
}
