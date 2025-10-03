// app/src/main/java/com/example/bhalobariwala/DatabaseHelper.java
package com.example.bhalobariwala;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Bump version when schema changes
    private static final String DATABASE_NAME = "bhalobariwala.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS   = "users";
    public static final String COL_ID        = "id";
    public static final String COL_NAME      = "name";
    public static final String COL_EMAIL     = "email";
    public static final String COL_PWD_HASH  = "password_hash";
    public static final String COL_SALT      = "salt";
    public static final String COL_ROLE      = "role";     // TENANT or OWNER
    public static final String COL_CREATED   = "created_at";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COL_PWD_HASH + " TEXT NOT NULL, " +
                    COL_SALT + " TEXT NOT NULL, " +
                    COL_ROLE + " TEXT NOT NULL CHECK (" + COL_ROLE + " IN ('TENANT','OWNER')), " +
                    COL_CREATED + " INTEGER NOT NULL" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COL_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_role  ON " + TABLE_USERS + "(" + COL_ROLE  + ")");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple dev strategy: reset table if moving from v1
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }
}
