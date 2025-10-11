package com.example.bhalobariwala;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "BhaloBariWalaPrefs";
    private static final String KEY_ID = "user_id";   // add this
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_ROLE = "user_role";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveLogin(int id, String email, String role) {
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    public void setLogin(long userId, String email, String roleStr) {
    }
}
