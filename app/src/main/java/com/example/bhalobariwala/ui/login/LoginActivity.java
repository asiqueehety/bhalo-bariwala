// app/src/main/java/com/example/bhalobariwala/ui/login/LoginActivity.java
package com.example.bhalobariwala.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.R;
import com.example.bhalobariwala.UserDAO;
import com.example.bhalobariwala.ui.owner.OwnerDashboardActivity;
import com.example.bhalobariwala.ui.tenant.TenantDashboardActivity;
import com.example.bhalobariwala.SignUpActivity;
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

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();

        userDAO = new UserDAO(this);
        userDAO.open();

        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(KEY_ROLE, Role.TENANT.name());
            try {
                selectedRole = Role.valueOf(saved);
            } catch (IllegalArgumentException ignore) {
                selectedRole = Role.TENANT;
            }
        }
        setUpRoleToggle();

        btnLogin.setOnClickListener(v -> attemptLogin());

        // "Create account" text -> open Sign Up screen
        findViewById(R.id.tvSignup).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );

        // "Forgot password" placeholder
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

        // Tiny delay to show the spinner; real apps would call into a repo/async op.
        etEmail.postDelayed(() -> {
            boolean ok = userDAO.validateLogin(
                    email,
                    password,
                    selectedRole == Role.TENANT ? "TENANT" : "OWNER"
            );
            setLoading(false);

            if (ok) {
                if (selectedRole == Role.TENANT) {
                    startActivity(new Intent(this, TenantDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, OwnerDashboardActivity.class));
                }
                finish(); // prevent back to login
            } else {
                Toast.makeText(this, "Invalid credentials or role", Toast.LENGTH_SHORT).show();
            }
        }, 250);
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
        userDAO.close();
    }
}
