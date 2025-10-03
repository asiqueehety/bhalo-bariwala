package com.example.bhalobariwala;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bhalobariwala.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable EdgeToEdge for modern UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system window insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Buttons
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> {
            // Start LoginActivity (make sure you create it)
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnSignup.setOnClickListener(v -> {
            // Start SignupActivity (make sure you create it)
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });
    }
}
