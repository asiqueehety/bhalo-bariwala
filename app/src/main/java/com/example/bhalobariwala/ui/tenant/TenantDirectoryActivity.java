package com.example.bhalobariwala.ui.tenant;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.DatabaseHelper;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;
import com.example.bhalobariwala.TenantDAO;

import java.util.ArrayList;
import java.util.List;

public class TenantDirectoryActivity extends AppCompatActivity {

    private RecyclerView rvDirectory;
    private TextView tvEmpty;
    private TenantDirectoryAdapter adapter;
    private TenantDAO tenantDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenant_directory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        rvDirectory = findViewById(R.id.rvDirectory);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        rvDirectory.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DAO and session
        tenantDAO = new TenantDAO(this);
        sessionManager = new SessionManager(this);

        // Load data
        loadBuildingDirectory();
    }

    private void loadBuildingDirectory() {
        tenantDAO.open();

        try {
            int tenantId = sessionManager.getUserId();
            if (tenantId == -1) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Get current tenant's information
            Cursor tenantCursor = tenantDAO.getTenantById(tenantId);
            if (!tenantCursor.moveToFirst()) {
                showEmpty("Tenant information not found");
                tenantCursor.close();
                return;
            }

            int propIdIndex = tenantCursor.getColumnIndex(DatabaseHelper.T_PROP_ID);
            if (propIdIndex == -1 || tenantCursor.isNull(propIdIndex)) {
                showEmpty("You are not assigned to any property");
                tenantCursor.close();
                return;
            }

            int propertyId = tenantCursor.getInt(propIdIndex);
            tenantCursor.close();

            // Get building (property) information
            BuildingInfo buildingInfo = getBuildingInfo(propertyId);
            if (buildingInfo == null) {
                showEmpty("Building information not found");
                return;
            }

            // Get all tenants in the same property
            List<TenantInfo> tenantList = getTenantsInProperty(propertyId);

            // Setup adapter
            if (adapter == null) {
                adapter = new TenantDirectoryAdapter(buildingInfo, tenantList);
                rvDirectory.setAdapter(adapter);
            } else {
                adapter.updateData(buildingInfo, tenantList);
            }

            // Show/hide empty state
            if (tenantList.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("No other tenants in this building");
            } else {
                tvEmpty.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("TenantDirectory", "Error loading directory", e);
            Toast.makeText(this, "Error loading directory: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showEmpty("Error loading directory");
        } finally {
            tenantDAO.close();
        }
    }

    private BuildingInfo getBuildingInfo(int propertyId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p." + DatabaseHelper.P_NAME + ", " +
                "p." + DatabaseHelper.P_ID + ", " +
                "l." + DatabaseHelper.L_NAME + ", " +
                "l." + DatabaseHelper.L_CONTACT + " " +
                "FROM " + DatabaseHelper.T_PROPERTY + " p " +
                "INNER JOIN " + DatabaseHelper.T_LANDLORD + " l " +
                "ON p." + DatabaseHelper.P_LANDLORDID + " = l." + DatabaseHelper.L_ID + " " +
                "WHERE p." + DatabaseHelper.P_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(propertyId)});

        BuildingInfo buildingInfo = null;
        if (cursor.moveToFirst()) {
            String buildingName = cursor.getString(0);
            int buildingId = cursor.getInt(1);
            String landlordName = cursor.getString(2);
            String landlordPhone = cursor.getString(3);

            buildingInfo = new BuildingInfo(buildingName, buildingId, landlordName, landlordPhone);
        }

        cursor.close();
        db.close();
        return buildingInfo;
    }

    private List<TenantInfo> getTenantsInProperty(int propertyId) {
        List<TenantInfo> tenantList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT " + DatabaseHelper.T_NAME + ", " +
                DatabaseHelper.T_CONTACT + ", " +
                DatabaseHelper.T_APT_ID + " " +
                "FROM " + DatabaseHelper.T_TENANT + " " +
                "WHERE " + DatabaseHelper.T_PROP_ID + " = ? " +
                "AND " + DatabaseHelper.T_APT_ID + " IS NOT NULL " +
                "ORDER BY " + DatabaseHelper.T_APT_ID;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(propertyId)});

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String contact = cursor.getString(1);
            int aptId = cursor.getInt(2);

            tenantList.add(new TenantInfo(name, contact, aptId));
        }

        cursor.close();
        db.close();
        return tenantList;
    }

    private void showEmpty(String message) {
        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        rvDirectory.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tenantDAO != null) {
            tenantDAO.close();
        }
    }
}