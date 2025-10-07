// app/src/main/java/com/example/bhalobariwala/ui/tenant/TenantDashboardActivity.java
package com.example.bhalobariwala.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.R;
import com.google.android.material.card.MaterialCardView;

public class TenantDashboardActivity extends AppCompatActivity {

    private MaterialCardView cardProfile, cardComplaints, cardDirectory;
    private MaterialCardView cardMessageLandlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_dashboard);

        // Bind UI
        cardProfile = findViewById(R.id.cardProfile);
        cardComplaints = findViewById(R.id.cardComplaints);
        cardDirectory = findViewById(R.id.cardDirectory);
        cardMessageLandlord = findViewById(R.id.cardMessageLandlord);

        // Click listeners
        cardProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TenantProfileActivity.class));
        });

        cardComplaints.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Complaints...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TenantComplaintsActivity.class));
        });

        cardDirectory.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Directory...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TenantDirectoryActivity.class));
        });

        // NEW: Message landlord click listener
        cardMessageLandlord.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Messages...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.bhalobariwala.ui.chat.TenantChatActivity.class));
        });
    }
}
