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
    private RadioGroup radioGroupRole;
    private Button btnSignUp;

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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String retypePassword = editRetypePassword.getText().toString().trim();

                int selectedId = radioGroupRole.getCheckedRadioButtonId();
                RadioButton selectedRoleBtn = findViewById(selectedId);
                String role = selectedRoleBtn != null ? selectedRoleBtn.getText().toString() : "";

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty() || role.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(retypePassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Signed up as " + role + "\nUsername: " + username + "\nEmail: " + email,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
