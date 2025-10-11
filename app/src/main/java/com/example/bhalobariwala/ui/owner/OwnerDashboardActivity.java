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
    private MaterialCardView cardMessages, cardApartments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        // Bind views
        cardProperties = findViewById(R.id.cardProperties);
        cardComplaints = findViewById(R.id.cardComplaints);
        cardOwnerProfile = findViewById(R.id.cardOwnerProfile);
        cardMessages = findViewById(R.id.cardMessages);
        cardApartments = findViewById(R.id.cardApartments);

        // Set click listeners
        cardProperties.setOnClickListener(v ->{
                        startActivity(new Intent(this, PropertiesActivity.class));}
        );

        cardComplaints.setOnClickListener(v ->{
                        startActivity(new Intent(this, ComplaintsActivity.class));}
        );

        cardOwnerProfile.setOnClickListener(v ->{
                        startActivity(new Intent(this, OwnerProfileActivity.class));}
        );

        // NEW: Messages click listener
        cardMessages.setOnClickListener(v -> {
                        startActivity(new Intent(this, com.example.bhalobariwala.ui.chat.LandlordMessagesActivity.class));
        });

        // NEW: Apartments click listener
        cardApartments.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewApartmentsActivity.class));
        });
    }
}
