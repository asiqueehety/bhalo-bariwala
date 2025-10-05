package com.example.bhalobariwala.ui.owner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.PropertyDAO;
import com.example.bhalobariwala.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddPropertyActivity extends AppCompatActivity {

    private static final String TAG = "AddPropertyActivity";

    private TextInputEditText etPropName;
    private MaterialButton btnSave, btnCancel;
    private PropertyDAO dao;
    private long landlordId = -1L; // l_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        etPropName = findViewById(R.id.etPropName);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
        landlordId = sp.getLong("current_landlord_id", -1L);
        Log.d(TAG, "current_landlord_id = " + landlordId);

        dao = new PropertyDAO(this);
        dao.open();

        btnSave.setOnClickListener(v -> save());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void save() {
        String name = etPropName.getText() != null ? etPropName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(name)) {
            etPropName.setError("Property name required");
            return;
        }
        if (landlordId == -1L) {
            Toast.makeText(this, "No landlord session. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            long rowId = dao.insert(name, landlordId); // sets l_id, prop_id auto-increments
            if (rowId == -1) {
                Toast.makeText(this, "Failed to add property", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Property added with ID: " + rowId);
                Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // back to list; onResume() refreshes it
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving property: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dao != null) dao.close();
    }
}
