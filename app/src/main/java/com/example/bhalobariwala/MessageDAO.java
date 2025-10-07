package com.example.bhalobariwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageDAO {

    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public MessageDAO(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    /** Send a message */
    public long sendMessage(int landlordId, int tenantId, String senderType, String message) {
        try {
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.M_LANDLORD_ID, landlordId);
            v.put(DatabaseHelper.M_TENANT_ID, tenantId);
            v.put(DatabaseHelper.M_SENDER_TYPE, senderType);
            v.put(DatabaseHelper.M_MESSAGE, message);
            v.put(DatabaseHelper.M_TIMESTAMP, System.currentTimeMillis());
            v.put(DatabaseHelper.M_IS_READ, 0);

            long result = db.insert(DatabaseHelper.T_MESSAGES, null, v);
            if (result == -1) {
                Log.e("MessageDAO", "Insert failed");
            }
            return result;
        } catch (Exception e) {
            Log.e("MessageDAO", "Exception during message insert", e);
            return -1;
        }
    }

    /** Get all messages between a landlord and tenant */
    public Cursor getConversation(int landlordId, int tenantId) {
        return db.query(
                DatabaseHelper.T_MESSAGES,
                null,
                DatabaseHelper.M_LANDLORD_ID + "=? AND " + DatabaseHelper.M_TENANT_ID + "=?",
                new String[]{String.valueOf(landlordId), String.valueOf(tenantId)},
                null, null,
                DatabaseHelper.M_TIMESTAMP + " ASC"
        );
    }

    /** Get list of tenants who messaged this landlord */
    public Cursor getTenantsWhoMessaged(int landlordId) {
        String query = "SELECT DISTINCT t." + DatabaseHelper.T_ID + ", " +
                "t." + DatabaseHelper.T_NAME + ", " +
                "MAX(m." + DatabaseHelper.M_TIMESTAMP + ") as last_message_time " +
                "FROM " + DatabaseHelper.T_TENANT + " t " +
                "INNER JOIN " + DatabaseHelper.T_MESSAGES + " m " +
                "ON t." + DatabaseHelper.T_ID + " = m." + DatabaseHelper.M_TENANT_ID + " " +
                "WHERE m." + DatabaseHelper.M_LANDLORD_ID + " = ? " +
                "GROUP BY t." + DatabaseHelper.T_ID + ", t." + DatabaseHelper.T_NAME + " " +
                "ORDER BY last_message_time DESC";

        return db.rawQuery(query, new String[]{String.valueOf(landlordId)});
    }

    /** Mark messages as read */
    public void markAsRead(int landlordId, int tenantId, String readerType) {
        try {
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.M_IS_READ, 1);

            String whereClause = DatabaseHelper.M_LANDLORD_ID + "=? AND " +
                                DatabaseHelper.M_TENANT_ID + "=? AND " +
                                DatabaseHelper.M_SENDER_TYPE + "!=?";

            db.update(
                    DatabaseHelper.T_MESSAGES,
                    v,
                    whereClause,
                    new String[]{String.valueOf(landlordId), String.valueOf(tenantId), readerType}
            );
        } catch (Exception e) {
            Log.e("MessageDAO", "Exception marking messages as read", e);
        }
    }

    /** Get unread message count for a user */
    public int getUnreadCount(int userId, String userType) {
        String whereClause;
        if (userType.equals("landlord")) {
            whereClause = DatabaseHelper.M_LANDLORD_ID + "=? AND " +
                         DatabaseHelper.M_SENDER_TYPE + "='tenant' AND " +
                         DatabaseHelper.M_IS_READ + "=0";
        } else {
            whereClause = DatabaseHelper.M_TENANT_ID + "=? AND " +
                         DatabaseHelper.M_SENDER_TYPE + "='landlord' AND " +
                         DatabaseHelper.M_IS_READ + "=0";
        }

        Cursor c = db.query(
                DatabaseHelper.T_MESSAGES,
                new String[]{"COUNT(*)"},
                whereClause,
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }
}

