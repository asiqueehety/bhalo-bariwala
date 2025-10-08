package com.example.bhalobariwala.ui.owner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bhalobariwala.ApartmentDAO;
import com.example.bhalobariwala.PropertyDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.model.Apartment;
import com.example.bhalobariwala.model.Property;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ViewApartmentsActivity extends AppCompatActivity {

    private LinearLayout buildingsContainer;
    private LinearLayout emptyState;
    private MaterialButton btnAddApartment;

    private PropertyDAO propertyDAO;
    private ApartmentDAO apartmentDAO;
    private long landlordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_apartments);

        // Get landlord ID from session
        landlordId = getSharedPreferences("auth", MODE_PRIVATE)
                .getLong("current_landlord_id", -1);

        if (landlordId == -1) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buildingsContainer = findViewById(R.id.buildingsContainer);
        emptyState = findViewById(R.id.emptyState);
        btnAddApartment = findViewById(R.id.btnAddApartment);

        propertyDAO = new PropertyDAO(this);
        propertyDAO.open();
        apartmentDAO = new ApartmentDAO(this);
        apartmentDAO.open();

        btnAddApartment.setOnClickListener(v -> showAddApartmentDialog());

        loadApartments();
    }

    private void loadApartments() {
        buildingsContainer.removeAllViews();

        // Get all properties for this landlord
        List<Property> properties = propertyDAO.listByLandlord(landlordId);

        if (properties.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        emptyState.setVisibility(View.GONE);

        // For each property, create a big card with apartments inside
        for (Property property : properties) {
            createPropertyCard(property);
        }
    }

    private void createPropertyCard(Property property) {
        // Create the big building card
        View propertyCardView = LayoutInflater.from(this)
                .inflate(R.layout.item_property_with_apartments, buildingsContainer, false);

        TextView txtPropertyName = propertyCardView.findViewById(R.id.txtPropertyName);
        TextView txtPropertyId = propertyCardView.findViewById(R.id.txtPropertyId);
        LinearLayout apartmentsContainer = propertyCardView.findViewById(R.id.apartmentsContainer);

        txtPropertyName.setText(property.getPropName());
        txtPropertyId.setText("Building ID: " + property.getPropId());

        // Get all apartments for this property
        List<Apartment> apartments = apartmentDAO.listByProperty(property.getPropId());

        if (apartments.isEmpty()) {
            // Show empty message
            TextView emptyText = new TextView(this);
            emptyText.setText("No apartments in this building");
            emptyText.setTextColor(0xFF999999);
            emptyText.setTextSize(14);
            emptyText.setPadding(16, 16, 16, 16);
            apartmentsContainer.addView(emptyText);
        } else {
            // Add apartment cards
            for (Apartment apartment : apartments) {
                View apartmentCard = createApartmentCard(apartment);
                apartmentsContainer.addView(apartmentCard);
            }
        }

        buildingsContainer.addView(propertyCardView);
    }

    private View createApartmentCard(Apartment apartment) {
        View cardView = LayoutInflater.from(this)
                .inflate(R.layout.item_apartment_card, buildingsContainer, false);

        TextView txtApartmentId = cardView.findViewById(R.id.txtApartmentId);
        TextView txtApartmentRent = cardView.findViewById(R.id.txtApartmentRent);
        TextView txtApartmentStatus = cardView.findViewById(R.id.txtApartmentStatus);
        View statusIndicator = cardView.findViewById(R.id.statusIndicator);

        txtApartmentId.setText("Apartment #" + apartment.getAptId());
        txtApartmentRent.setText("Rent: $" + apartment.getRent());

        // Check if apartment is occupied
        boolean isOccupied = apartmentDAO.isApartmentOccupied(apartment.getAptId());

        if (isOccupied) {
            txtApartmentStatus.setText("Occupied");
            txtApartmentStatus.setTextColor(0xFFD32F2F); // Red
            statusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFD32F2F)); // Red
        } else {
            txtApartmentStatus.setText("Vacant");
            txtApartmentStatus.setTextColor(0xFF4CAF50); // Green
            statusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
        }

        return cardView;
    }

    private void showAddApartmentDialog() {
        // Get properties for dropdown
        List<Property> properties = propertyDAO.listByLandlord(landlordId);

        if (properties.isEmpty()) {
            Toast.makeText(this, "Please add a property first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_apartment, null);

        android.widget.Spinner spinnerProperty = dialogView.findViewById(R.id.spinnerProperty);
        TextInputEditText editRent = dialogView.findViewById(R.id.editRent);

        // Populate spinner with properties
        String[] propertyNames = new String[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            propertyNames[i] = properties.get(i).getPropName() + " (ID: " + properties.get(i).getPropId() + ")";
        }

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, propertyNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProperty.setAdapter(adapter);

        builder.setView(dialogView)
                .setTitle("Add New Apartment")
                .setPositiveButton("Add", (dialog, which) -> {
                    int selectedPosition = spinnerProperty.getSelectedItemPosition();
                    Property selectedProperty = properties.get(selectedPosition);

                    if (editRent.getText() == null || editRent.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Please enter rent amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String rentStr = editRent.getText().toString().trim();
                    double rent;
                    try {
                        rent = Double.parseDouble(rentStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid rent amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long result = apartmentDAO.insert(selectedProperty.getPropId(), rent);

                    if (result > 0) {
                        Toast.makeText(this, "Apartment added successfully!", Toast.LENGTH_SHORT).show();
                        loadApartments(); // Refresh the list
                    } else {
                        Toast.makeText(this, "Failed to add apartment", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        propertyDAO.close();
        apartmentDAO.close();
    }
}
