package com.example.bhalobariwala;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No setContentView() so we don't need activity_main.xml
        startActivity(new Intent(this, com.example.bhalobariwala.ui.login.LoginActivity.class));
        finish();
    }
}
