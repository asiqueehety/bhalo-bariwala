package com.example.bhalobariwala.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.LandlordDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;
import com.example.bhalobariwala.SignUpActivity;
import com.example.bhalobariwala.TenantDAO;
import com.example.bhalobariwala.ui.owner.OwnerDashboardActivity;
import com.example.bhalobariwala.ui.tenant.TenantDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String KEY_ROLE = "key_role";

    private MaterialButtonToggleGroup roleToggle;
    private MaterialButton btnLogin;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private CircularProgressIndicator progress;

    private enum Role { TENANT, OWNER }
    private Role selectedRole = Role.TENANT;

    private LandlordDAO landlordDAO;
    private TenantDAO tenantDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();

        landlordDAO = new LandlordDAO(this); landlordDAO.open();
        tenantDAO   = new TenantDAO(this);   tenantDAO.open();

        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(KEY_ROLE, Role.TENANT.name());
            try { selectedRole = Role.valueOf(saved); } catch (IllegalArgumentException ignore) { selectedRole = Role.TENANT; }
        }
        setUpRoleToggle();

        btnLogin.setOnClickListener(v -> attemptLogin());

        findViewById(R.id.tvSignup).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );

        findViewById(R.id.tvForgot).setOnClickListener(v ->
                Toast.makeText(this, "Forgot password not implemented in local demo", Toast.LENGTH_SHORT).show()
        );
    }

    private void bindViews() {
        roleToggle = findViewById(R.id.roleToggle);
        btnLogin = findViewById(R.id.btnLogin);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progress = findViewById(R.id.progress);
    }

    private void setUpRoleToggle() {
        int preselectId = (selectedRole == Role.TENANT) ? R.id.btnTenant : R.id.btnOwner;
        roleToggle.check(preselectId);
        roleToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnTenant) selectedRole = Role.TENANT;
            else if (checkedId == R.id.btnOwner) selectedRole = Role.OWNER;
        });
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            cancel = true;
        } else if (password.length() < 6) {
            tilPassword.setError("At least 6 characters");
            cancel = true;
        }

        if (roleToggle.getCheckedButtonId() == View.NO_ID) {
            Toast.makeText(this, "Please choose a role", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) return;

        setLoading(true);

        // Simulate async work
        etEmail.postDelayed(() -> {
            int userId = (selectedRole == Role.OWNER)
                    ? landlordDAO.validate(email, password)
                    : tenantDAO.validate(email, password);

            setLoading(false);

            if (userId != -1) {
                // Save ID, email, and role in session
                SessionManager session = new SessionManager(this);
                session.saveLogin(userId, email, selectedRole.name());

                // Navigate to dashboard
                if (selectedRole == Role.TENANT) {
                    // Resolve tenant id from your DAO (ensure this exists in your DAO)
                    long tenantIdFromDB = tenantDAO.getIdByEmail(email); // <-- implement if missing

                    // Store for later filtering/use
                    getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putLong("current_tenant_id", tenantIdFromDB)
                            .apply();

                    startActivity(new Intent(this, TenantDashboardActivity.class));
                } else {
                    // Resolve landlord id from your DAO (ensure this exists in your DAO)
                    long landlordIdFromDB = landlordDAO.getIdByEmail(email); // <-- implement if missing

                    // Store for later filtering/use
                    getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putLong("current_landlord_id", landlordIdFromDB)
                            .apply();

                    startActivity(new Intent(this, OwnerDashboardActivity.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }, 200);
    }


    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        roleToggle.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_ROLE, selectedRole.name());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        landlordDAO.close();
        tenantDAO.close();
    }
}
