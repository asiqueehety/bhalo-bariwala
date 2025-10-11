package com.example.bhalobariwala.ui.tenant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.ComplaintDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;             // <-- your SessionManager is in root package
import com.example.bhalobariwala.adapters.ComplaintsAdapter;
import com.example.bhalobariwala.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TenantComplaintsActivity extends AppCompatActivity {

    private ComplaintDAO dao;
    private ComplaintsAdapter adapter;
    private long tenantId = -1;
    private String role = "";
    private RecyclerView rv; // cache view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_complaints);
        setTitle(getString(R.string.tenant_complaints_title));

        // 1) Check session first
       SessionManager sm = new SessionManager(this);
//        if (!sm.isLoggedIn()) {
//            goLogin("Please log in.");
//            return;
//        }
//        role = sm.getRole();
      //  SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
//        tenantId = prefs.getLong("current_tenant_id", -1);
               tenantId = sm.getUserId(); // long-friendly

//        if (!"tenant".equalsIgnoreCase(role) || tenantId <= 0) {
//            goLogin("Tenant session not found. Please log in as a tenant.");
//            return;
//        }

        // 2) UI
        rv = findViewById(R.id.recyclerComplaints);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintsAdapter(this, new ArrayList<>());
        rv.setAdapter(adapter);

        // 3) DAO + initial load
        try {
            dao = new ComplaintDAO(this);
            dao.open();
            refreshList();
        } catch (Throwable t) {
            Toast.makeText(this, "Error loading complaints: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_complaint, null, false);
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDesc  = view.findViewById(R.id.etDesc);
        Spinner  spType  = view.findViewById(R.id.spType);

        new AlertDialog.Builder(this)
                .setTitle(R.string.complaint_add)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc  = etDesc.getText().toString().trim();
                    String type  = (spType != null && spType.getSelectedItem() != null)
                            ? spType.getSelectedItem().toString()
                            : "";

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Title is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        long id = dao.addComplaint(tenantId, title, desc, type);
                        if (id > 0) {
                            Toast.makeText(this, "Complaint added.", Toast.LENGTH_SHORT).show();
                            refreshList();
                        } else {
                            Toast.makeText(this, "Failed to add complaint.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Throwable t) {
                        Toast.makeText(this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void refreshList() {
        try {
            List<Map<String, String>> list = dao.getComplaintsForBuilding(tenantId);
            adapter = new ComplaintsAdapter(this, list);
            rv.setAdapter(adapter);

            if (list.isEmpty()) {
                Toast.makeText(this, getString(R.string.complaint_empty), Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable t) {
            Toast.makeText(this, "Load error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goLogin(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (dao != null) {
            try { dao.close(); } catch (Throwable ignored) {}
        }
        super.onDestroy();
    }
}
