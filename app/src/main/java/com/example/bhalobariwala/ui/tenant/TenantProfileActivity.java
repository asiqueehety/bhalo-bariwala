package com.example.bhalobariwala.ui.tenant;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bhalobariwala.DatabaseHelper;
import com.example.bhalobariwala.ui.login.LoginActivity;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.TenantDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TenantProfileActivity extends AppCompatActivity {

    private EditText etTenantName, etTenantEmail, etTenantContact, etTenantPropertyId, etTenantApartmentId;
    private TextView tvTenantCreatedDate;
    private Button btnEditProfile, btnChangePassword, btnLogout;

    private TenantDAO tenantDAO;
    private long tenantId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenant_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupDatabase();
        loadTenantData();
        setupButtons();
    }

    private void initializeViews() {
        etTenantName = findViewById(R.id.etTenantName);
        etTenantEmail = findViewById(R.id.etTenantEmail);
        etTenantContact = findViewById(R.id.etTenantContact);
        etTenantPropertyId = findViewById(R.id.etTenantPropertyId);
        etTenantApartmentId = findViewById(R.id.etTenantApartmentId);
        tvTenantCreatedDate = findViewById(R.id.tvTenantCreatedDate);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupDatabase() {
        tenantDAO = new TenantDAO(this);
        tenantDAO.open();

        // Get tenant ID from SharedPreferences (matching LoginActivity)
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        tenantId = prefs.getLong("current_tenant_id", -1);

        if (tenantId == -1) {
            Toast.makeText(this, "Error: No tenant session found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadTenantData() {
        Cursor cursor = tenantDAO.getTenantById(tenantId);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Get column indices
                int nameIdx = cursor.getColumnIndex(DatabaseHelper.T_NAME);
                int emailIdx = cursor.getColumnIndex(DatabaseHelper.T_EMAIL);
                int contactIdx = cursor.getColumnIndex(DatabaseHelper.T_CONTACT);
                int propIdIdx = cursor.getColumnIndex(DatabaseHelper.T_PROP_ID);
                int aptIdIdx = cursor.getColumnIndex(DatabaseHelper.T_APT_ID);
                int createdIdx = cursor.getColumnIndex(DatabaseHelper.T_CREATED);

                // Set data to views
                if (nameIdx != -1) etTenantName.setText(cursor.getString(nameIdx));
                if (emailIdx != -1) etTenantEmail.setText(cursor.getString(emailIdx));
                if (contactIdx != -1) etTenantContact.setText(cursor.getString(contactIdx));

                if (propIdIdx != -1 && !cursor.isNull(propIdIdx)) {
                    etTenantPropertyId.setText(cursor.getString(propIdIdx));
                }

                if (aptIdIdx != -1 && !cursor.isNull(aptIdIdx)) {
                    etTenantApartmentId.setText(cursor.getString(aptIdIdx));
                }

                if (createdIdx != -1) {
                    long created = cursor.getLong(createdIdx);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    tvTenantCreatedDate.setText(sdf.format(new Date(created)));
                }
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Failed to load tenant data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        btnEditProfile.setOnClickListener(v -> {
            if (isEditMode) {
                saveProfile();
            } else {
                enableEditMode();
            }
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnLogout.setOnClickListener(v -> logout());
    }

    private void enableEditMode() {
        isEditMode = true;

        // Enable editable fields
        etTenantName.setEnabled(true);
        etTenantContact.setEnabled(true);
        etTenantPropertyId.setEnabled(true);
        etTenantApartmentId.setEnabled(true);

        // Email is not editable
        etTenantEmail.setEnabled(false);

        // Change button text
        btnEditProfile.setText("Save Profile");
        btnEditProfile.setBackgroundTintList(getColorStateList(android.R.color.holo_orange_dark));
    }

    private void disableEditMode() {
        isEditMode = false;

        // Disable all fields
        etTenantName.setEnabled(false);
        etTenantEmail.setEnabled(false);
        etTenantContact.setEnabled(false);
        etTenantPropertyId.setEnabled(false);
        etTenantApartmentId.setEnabled(false);

        // Change button text back
        btnEditProfile.setText("Edit Profile");
        btnEditProfile.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
    }

    private void saveProfile() {
        String name = etTenantName.getText().toString().trim();
        String contact = etTenantContact.getText().toString().trim();
        String propId = etTenantPropertyId.getText().toString().trim();
        String aptId = etTenantApartmentId.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etTenantName.setError("Name is required");
            etTenantName.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            etTenantContact.setError("Contact is required");
            etTenantContact.requestFocus();
            return;
        }

        // Update in database
        boolean success = tenantDAO.updateTenant(tenantId, name, contact,
                propId.isEmpty() ? null : propId,
                aptId.isEmpty() ? null : aptId);

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            disableEditMode();
        } else {
            Toast.makeText(this, "Failed to update profile. Check property/apartment IDs.", Toast.LENGTH_LONG).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        builder.setTitle("Change Password");
        builder.setPositiveButton("Change", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to prevent auto-dismiss
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (oldPassword.isEmpty()) {
                etOldPassword.setError("Required");
                return;
            }

            if (newPassword.isEmpty()) {
                etNewPassword.setError("Required");
                return;
            }

            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            boolean success = tenantDAO.updatePassword(tenantId, oldPassword, newPassword);

            if (success) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to change password. Check your old password.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear session
                    SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    // Go to login
                    Intent intent = new Intent(TenantProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tenantDAO != null) {
            tenantDAO.close();
        }
    }
}