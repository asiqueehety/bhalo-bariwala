package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintDAO {
    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public ComplaintDAO(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    public void open() { db = helper.getWritableDatabase(); }
    public void close() { if (db != null && db.isOpen()) db.close(); }

    public long addComplaint(String title, String desc, String type, long tenantId) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.C_TITLE, title);
        v.put(DatabaseHelper.C_DESC, desc);
        v.put(DatabaseHelper.C_TYPE, type);
        v.put(DatabaseHelper.C_TID, tenantId);
        return db.insert(DatabaseHelper.T_COMPLAINTS, null, v);
    }

    public List<Map<String, String>> getComplaintsForBuilding(long tenantId) {
        String query = "SELECT c." + DatabaseHelper.C_TITLE + ", c." + DatabaseHelper.C_DESC + ", c." + DatabaseHelper.C_TYPE +
                ", t." + DatabaseHelper.T_APT_ID +
                " FROM " + DatabaseHelper.T_COMPLAINTS + " c " +
                " INNER JOIN " + DatabaseHelper.T_TENANT + " t ON c." + DatabaseHelper.C_TID + " = t." + DatabaseHelper.T_ID +
                " WHERE t." + DatabaseHelper.T_PROP_ID + " = (" +
                "SELECT " + DatabaseHelper.T_PROP_ID + " FROM " + DatabaseHelper.T_TENANT +
                " WHERE " + DatabaseHelper.T_ID + " = ?)";
        Cursor cur = db.rawQuery(query, new String[]{String.valueOf(tenantId)});
        List<Map<String, String>> list = new ArrayList<>();
        while (cur.moveToNext()) {
            Map<String, String> m = new HashMap<>();
            m.put("title", cur.getString(0));
            m.put("desc", cur.getString(1));
            m.put("type", cur.getString(2));
            m.put("apt_id", cur.getString(3));
            list.add(m);
        }
        cur.close();
        return list;
    }
}
