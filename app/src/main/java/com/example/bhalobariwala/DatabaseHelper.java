// app/src/main/java/com/example/bhalobariwala/DatabaseHelper.java
package com.example.bhalobariwala;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // === DB meta ===
    private static final String DATABASE_NAME = "bhalobariwala.db";
    // Bump when schema changes
    private static final int DATABASE_VERSION = 4;

    // === LANDLORD ===
    public static final String T_LANDLORD   = "landlord";
    public static final String L_ID         = "l_id";
    public static final String L_NAME       = "l_name";
    public static final String L_CONTACT    = "l_contact";
    public static final String L_EMAIL      = "l_email";
    public static final String L_PWD_HASH   = "l_password_hash";
    public static final String L_SALT       = "l_salt";
    public static final String L_CREATED    = "l_created";

    // === TENANT ===
    public static final String T_TENANT     = "tenant";
    public static final String T_ID         = "t_id";
    public static final String T_NAME       = "t_name";
    public static final String T_CONTACT    = "t_contact";
    public static final String T_EMAIL      = "t_email";
    public static final String T_PWD_HASH   = "t_password_hash";
    public static final String T_SALT       = "t_salt";
    public static final String T_CREATED    = "t_created";
    // Relations (nullable at signup)
    public static final String T_PROP_ID    = "t_prop_id";  // -> property.prop_id
    public static final String T_APT_ID     = "t_apt_id";   // -> apartment.apt_id (1:1 idea)
    public static final String T_LID        = "t_lid";      // -> landlord.l_id

    // === PROPERTY ===
    public static final String T_PROPERTY   = "property";
    public static final String P_ID         = "prop_id";
    public static final String P_NAME       = "prop_name";
    public static final String P_LANDLORDID = "landlord_id"; // -> landlord.l_id

    // === APARTMENT ===
    public static final String T_APARTMENT  = "apartment";
    public static final String A_ID         = "apt_id";
    public static final String A_PROP_ID    = "a_prop_id"; // -> property.prop_id
    public static final String A_RENT       = "a_rent";

    // === COMPLAINTS ===
    public static final String T_COMPLAINTS = "complaints";
    public static final String C_ID         = "c_id";
    public static final String C_TITLE      = "c_title";
    public static final String C_DESC       = "c_desc";
    public static final String C_TID        = "c_tid";   // -> tenant.t_id
    public static final String C_TYPE       = "c_type";  // {electricity, gas, water, security, maintenance}

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Turn on foreign key enforcement
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // same as PRAGMA foreign_keys=ON
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // --- Landlord table ---
        db.execSQL(
                "CREATE TABLE " + T_LANDLORD + " (" +
                        L_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        L_NAME + " TEXT NOT NULL, " +
                        L_CONTACT + " TEXT, " +
                        L_EMAIL + " TEXT NOT NULL UNIQUE, " +
                        L_PWD_HASH + " TEXT NOT NULL, " +
                        L_SALT + " TEXT NOT NULL, " +
                        L_CREATED + " INTEGER NOT NULL" +
                        ")"
        );

        // --- Tenant table ---
        db.execSQL(
                "CREATE TABLE " + T_TENANT + " (" +
                        T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        T_NAME + " TEXT NOT NULL, " +
                        T_CONTACT + " TEXT, " +
                        T_EMAIL + " TEXT NOT NULL UNIQUE, " +
                        T_PWD_HASH + " TEXT NOT NULL, " +
                        T_SALT + " TEXT NOT NULL, " +
                        T_CREATED + " INTEGER NOT NULL, " +
                        T_PROP_ID + " INTEGER, " +
                        T_APT_ID + " INTEGER UNIQUE, " +  // 1:1-ish (each apt linked to at most one tenant)

                        "FOREIGN KEY(" + T_PROP_ID + ") REFERENCES " + T_PROPERTY + "(" + P_ID + ") ON UPDATE CASCADE ON DELETE SET NULL, " +
                        "FOREIGN KEY(" + T_APT_ID + ") REFERENCES " + T_APARTMENT + "(" + A_ID + ") ON UPDATE CASCADE ON DELETE SET NULL " +
                        ")"
        );

        // --- Property table ---
        db.execSQL(
                "CREATE TABLE " + T_PROPERTY + " (" +
                        P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        P_NAME + " TEXT NOT NULL, " +
                        P_LANDLORDID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + P_LANDLORDID + ") REFERENCES " + T_LANDLORD + "(" + L_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                        ")"
        );

        // --- Apartment table ---
        db.execSQL(
                "CREATE TABLE " + T_APARTMENT + " (" +
                        A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        A_PROP_ID + " INTEGER NOT NULL, " +
                        A_RENT + " REAL NOT NULL, " +
                        "FOREIGN KEY(" + A_PROP_ID + ") REFERENCES " + T_PROPERTY + "(" + P_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                        ")"
        );

        // --- Complaints table ---
        db.execSQL(
                "CREATE TABLE " + T_COMPLAINTS + " (" +
                        C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        C_TITLE + " TEXT NOT NULL, " +
                        C_DESC + " TEXT, " +
                        C_TID + " INTEGER NOT NULL, " +
                        C_TYPE + " TEXT NOT NULL CHECK(" + C_TYPE + " IN ('electricity','gas','water','security','maintenance')), " +
                        "FOREIGN KEY(" + C_TID + ") REFERENCES " + T_TENANT + "(" + T_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                        ")"
        );

        // --- Indexes ---
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_landlord_email ON " + T_LANDLORD + "(" + L_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tenant_email   ON " + T_TENANT   + "(" + T_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_prop_landlord  ON " + T_PROPERTY + "(" + P_LANDLORDID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_apt_prop       ON " + T_APARTMENT+ "(" + A_PROP_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tenant_links   ON " + T_TENANT   + "(" + T_PROP_ID + "," + T_APT_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_complaints_tid ON " + T_COMPLAINTS+ "(" + C_TID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Because this is a significant schema change (1 users table -> 2 auth tables + domain tables),
        // simplest dev approach: drop and recreate. If you need migration, we can add it later.
        db.execSQL("PRAGMA foreign_keys=OFF");
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + T_COMPLAINTS);
            db.execSQL("DROP TABLE IF EXISTS " + T_APARTMENT);
            db.execSQL("DROP TABLE IF EXISTS " + T_PROPERTY);
            db.execSQL("DROP TABLE IF EXISTS " + T_TENANT);
            db.execSQL("DROP TABLE IF EXISTS " + T_LANDLORD);
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }
}
