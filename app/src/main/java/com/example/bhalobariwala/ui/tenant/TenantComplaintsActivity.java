package com.example.bhalobariwala.ui.tenant;

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
import com.example.bhalobariwala.adapters.ComplaintsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

public class TenantComplaintsActivity extends AppCompatActivity {
    private ComplaintDAO dao;
    private ComplaintsAdapter adapter;
    private long tenantId; // assume you pass this via Intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_complaints);

        tenantId = getSharedPreferences("auth", MODE_PRIVATE)
                .getLong("current_tenant_id", -1);
        dao = new ComplaintDAO(this);
        dao.open();

        RecyclerView rv = findViewById(R.id.recyclerComplaints);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Map<String, String>> list = dao.getComplaintsForBuilding(tenantId);
        adapter = new ComplaintsAdapter(this, list);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_complaint, null);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDesc = dialogView.findViewById(R.id.etDesc);
        Spinner spType = dialogView.findViewById(R.id.spType);

        new AlertDialog.Builder(this)
                .setTitle("Add Complaint")
                .setView(dialogView)
                .setPositiveButton("Submit", (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String type = spType.getSelectedItem().toString();

                    if (title.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dao.addComplaint(title, desc, type, tenantId);
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshList() {
        List<Map<String, String>> list = dao.getComplaintsForBuilding(tenantId);
        adapter = new ComplaintsAdapter(this, list);
        RecyclerView rv = findViewById(R.id.recyclerComplaints);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
