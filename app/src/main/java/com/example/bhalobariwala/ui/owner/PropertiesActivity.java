package com.example.bhalobariwala.ui.owner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.PropertyDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.model.Property;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PropertiesActivity extends AppCompatActivity {

    private static final String TAG = "PropertiesActivity";

    private RecyclerView rv;
    private TextView tvEmpty;
    private FloatingActionButton fab;
    private PropertiesAdapter adapter;
    private PropertyDAO dao;
    private long landlordId = -1L; // l_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        rv = findViewById(R.id.rvProperties);
        tvEmpty = findViewById(R.id.tvEmpty);
        fab = findViewById(R.id.fabAddProperty);

        // 1) get l_id from SharedPreferences (set in LoginActivity after success)
        SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
        landlordId = sp.getLong("current_landlord_id", -1L);
        Log.d(TAG, "current_landlord_id = " + landlordId);
        if (landlordId == -1L) {
            Toast.makeText(this, "No landlord session. Please login first.", Toast.LENGTH_LONG).show();
        }

        // 2) DAO + Recycler
        dao = new PropertyDAO(this);

        adapter = new PropertiesAdapter(new ArrayList<>());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        // 3) FAB → Add property
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddPropertyActivity.class))
        );
    }

    private void loadData() {
        try {

            // Load properties for this landlord
            List<Property> data = (landlordId == -1L) ? new ArrayList<>() : dao.listByLandlord(landlordId);
            Log.d(TAG, "Loaded " + data.size() + " properties for landlord " + landlordId);
            int totalCount = dao.countAll();

            adapter.setItems(data);

            boolean empty = data.isEmpty();
            rv.setVisibility(empty ? View.GONE : View.VISIBLE);
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);

            // Update empty message to show landlord ID
            if (empty) {
                tvEmpty.setText("No properties found for Landlord ID: " + landlordId +
                        "\nTotal properties in database: " + totalCount);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading data: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading properties: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reopen database and refresh data
        if (dao != null) {
            dao.open();
        }
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // close database when activity is paused
        if (dao != null) {
            dao.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dao != null) {
            dao.close();
        }
    }
}
