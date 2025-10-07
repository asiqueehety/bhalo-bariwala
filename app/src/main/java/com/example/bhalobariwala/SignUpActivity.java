// app/src/main/java/com/example/bhalobariwala/SignUpActivity.java
package com.example.bhalobariwala;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editEmail, editPassword, editRetypePassword;
    private TextInputEditText editContact, editBuildingId, editApartmentId;
    private RadioGroup radioGroupRole;
    private Button btnSignUp;
    private View buildingIdContainer;

    private LandlordDAO landlordDAO;
    private TenantDAO tenantDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editRetypePassword = findViewById(R.id.editRetypePassword);
        editContact = findViewById(R.id.editContact);
        editBuildingId = findViewById(R.id.editBuildingId);
        editApartmentId = findViewById(R.id.editApartmentId);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        buildingIdContainer = findViewById(R.id.buildingIdContainer);

        // Initialize DAOs
        landlordDAO = new LandlordDAO(this);
        landlordDAO.open();
        tenantDAO = new TenantDAO(this);
        tenantDAO.open();

        // Show/hide tenant-only fields
        radioGroupRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTenant) {
                buildingIdContainer.setVisibility(View.VISIBLE);
            } else {
                buildingIdContainer.setVisibility(View.GONE);
            }
        });

        // Sign up logic
        btnSignUp.setOnClickListener(v -> {
            String username = safe(editUsername);
            String email = safe(editEmail);
            String password = safe(editPassword);
            String retype = safe(editRetypePassword);
            String contact = safe(editContact);
            String buildingId = safe(editBuildingId);
            String apartmentId = safe(editApartmentId);

            int selectedId = radioGroupRole.getCheckedRadioButtonId();
            RadioButton selectedRoleBtn = findViewById(selectedId);
            String role = selectedRoleBtn != null ? selectedRoleBtn.getText().toString().toUpperCase() : "";

            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retype.isEmpty() || contact.isEmpty()) {
                toast("Please fill all required fields");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                toast("Enter a valid email");
                return;
            }
            if (password.length() < 6) {
                toast("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(retype)) {
                toast("Passwords do not match");
                return;
            }
            if (role.equals("TENANT")) {
                if (buildingId.isEmpty() || apartmentId.isEmpty()) {
                    toast("Please enter Building and Apartment ID");
                    return;
                }
            }

            long id;
            if (role.equals("OWNER")) {
                if (landlordDAO.emailExists(email)) {
                    toast("Email already registered (Owner)");
                    return;
                }
                id = landlordDAO.create(username, email, password, contact);
            } else {
                if (tenantDAO.emailExists(email)) {
                    toast("Email already registered (Tenant)");
                    return;
                }
                id = tenantDAO.create(username, email, password, contact, buildingId, apartmentId);
            }

            if (id > 0) {
                toast("Account created! You can log in now.");
                finish();
            } else {
                toast("Sign up failed. Try again. 223232");
            }
        });
    }

    private static String safe(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        landlordDAO.close();
        tenantDAO.close();
    }
}
