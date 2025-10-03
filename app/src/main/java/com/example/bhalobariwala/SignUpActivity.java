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

    private TextInputEditText editName, editEmail, editPassword;
    private RadioGroup radioGroupRole;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                int selectedId = radioGroupRole.getCheckedRadioButtonId();
                RadioButton selectedRoleBtn = findViewById(selectedId);
                String role = selectedRoleBtn != null ? selectedRoleBtn.getText().toString() : "";

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Signed up as " + role + "\nName: " + name + "\nEmail: " + email,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
