package com.example.bhalobariwala.ui.chat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.DatabaseHelper;
import com.example.bhalobariwala.MessageDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class LandlordMessagesActivity extends AppCompatActivity {

    private RecyclerView rvTenants;
    private TextView tvEmpty;

    private TenantListAdapter adapter;
    private MessageDAO messageDAO;
    private SessionManager sessionManager;

    private int landlordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_messages);

        // Initialize views
        rvTenants = findViewById(R.id.rvTenants);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        rvTenants.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DAO
        messageDAO = new MessageDAO(this);
        sessionManager = new SessionManager(this);

        // Get landlord ID
        landlordId = sessionManager.getUserId();
        if (landlordId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load tenants
        loadTenants();
    }

    private void loadTenants() {
        messageDAO.open();

        List<TenantListAdapter.TenantItem> tenants = new ArrayList<>();
        Cursor cursor = messageDAO.getTenantsWhoMessaged(landlordId);

        if (cursor.moveToFirst()) {
            do {
                int tenantId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.T_ID));
                String tenantName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.T_NAME));

                tenants.add(new TenantListAdapter.TenantItem(tenantId, tenantName));
            } while (cursor.moveToNext());
        }
        cursor.close();
        messageDAO.close();

        // Show/hide empty view
        if (tenants.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTenants.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTenants.setVisibility(View.VISIBLE);
        }

        // Setup adapter
        adapter = new TenantListAdapter(tenants, (tenantId, tenantName) -> {
            // Open chat with this tenant
            Intent intent = new Intent(LandlordMessagesActivity.this, LandlordChatActivity.class);
            intent.putExtra("TENANT_ID", tenantId);
            intent.putExtra("TENANT_NAME", tenantName);
            startActivity(intent);
        });
        rvTenants.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTenants();
    }
}

