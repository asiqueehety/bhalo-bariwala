// app/src/main/java/com/example/bhalobariwala/SignUpActivity.java
package com.example.bhalobariwala;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editEmail, editPassword, editRetypePassword;
    private RadioGroup radioGroupRole;
    private Button btnSignUp;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editRetypePassword = findViewById(R.id.editRetypePassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnSignUp = findViewById(R.id.btnSignUp);

        userDAO = new UserDAO(this);
        userDAO.open();

        btnSignUp.setOnClickListener(v -> {
            String username = safe(editUsername);
            String email = safe(editEmail);
            String password = safe(editPassword);
            String retype = safe(editRetypePassword);

            int selectedId = radioGroupRole.getCheckedRadioButtonId();
            RadioButton selectedRoleBtn = findViewById(selectedId);
            String role = selectedRoleBtn != null ? selectedRoleBtn.getText().toString().toUpperCase() : "";

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retype.isEmpty() || role.isEmpty()) {
                toast("Please fill all fields");
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
            if (!role.equals("OWNER") && !role.equals("TENANT")) {
                toast("Please select a role");
                return;
            }
            if (userDAO.emailExists(email)) {
                toast("Email already registered");
                return;
            }

            long id = userDAO.createUser(username, email, password, role);
            if (id > 0) {
                toast("Account created! You can log in now.");
                finish(); // go back to Login
            } else {
                toast("Sign up failed. Try again.");
            }
        });
    }

    private static String safe(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDAO.close();
    }
}
