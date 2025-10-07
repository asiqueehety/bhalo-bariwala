// app/src/main/java/com/example/bhalobariwala/ui/owner/OwnerDashboardActivity.java
package com.example.bhalobariwala.ui.owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.R;
import com.google.android.material.card.MaterialCardView;

public class OwnerDashboardActivity extends AppCompatActivity {

    private MaterialCardView cardProperties, cardRentCheck, cardComplaints, cardOwnerProfile;
    private MaterialCardView cardMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        // Bind views
        cardProperties = findViewById(R.id.cardProperties);
        cardRentCheck = findViewById(R.id.cardRentCheck);
        cardComplaints = findViewById(R.id.cardComplaints);
        cardOwnerProfile = findViewById(R.id.cardOwnerProfile);
        cardMessages = findViewById(R.id.cardMessages);

        // Set click listeners
        cardProperties.setOnClickListener(v ->{
                        Toast.makeText(this, "Properties clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, PropertiesActivity.class));}
        );

        cardRentCheck.setOnClickListener(v ->{
                        Toast.makeText(this, "Rent Check clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, RentCheckActivity.class));}
        );

        cardComplaints.setOnClickListener(v ->{
                        Toast.makeText(this, "Complaints clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ComplaintsActivity.class));}
        );

        cardOwnerProfile.setOnClickListener(v ->{
                        Toast.makeText(this, "Owner Profile clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, OwnerProfileActivity.class));}
        );

        // NEW: Messages click listener
        cardMessages.setOnClickListener(v -> {
                        Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, com.example.bhalobariwala.ui.chat.LandlordMessagesActivity.class));
        });
    }
}
