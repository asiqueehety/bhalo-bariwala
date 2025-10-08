// app/src/main/java/com/example/bhalobariwala/SignUpActivity.java
package com.example.bhalobariwala;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.model.Apartment;
import com.example.bhalobariwala.model.Property;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editEmail, editPassword, editRetypePassword;
    private TextInputEditText editContact;
    private Spinner spinnerBuilding, spinnerApartment;
    private RadioGroup radioGroupRole;
    private Button btnSignUp;
    private View buildingIdContainer;

    private LandlordDAO landlordDAO;
    private TenantDAO tenantDAO;
    private PropertyDAO propertyDAO;
    private ApartmentDAO apartmentDAO;

    private List<Property> propertyList = new ArrayList<>();
    private List<Apartment> apartmentList = new ArrayList<>();
    private long selectedPropertyId = -1;
    private long selectedApartmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editRetypePassword = findViewById(R.id.editRetypePassword);
        editContact = findViewById(R.id.editContact);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerApartment = findViewById(R.id.spinnerApartment);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        buildingIdContainer = findViewById(R.id.buildingIdContainer);

        // Initialize DAOs
        landlordDAO = new LandlordDAO(this);
        landlordDAO.open();
        tenantDAO = new TenantDAO(this);
        tenantDAO.open();
        propertyDAO = new PropertyDAO(this);
        propertyDAO.open();
        apartmentDAO = new ApartmentDAO(this);
        apartmentDAO.open();

        // Load properties for spinner
        loadProperties();

        // Show/hide tenant-only fields
        radioGroupRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTenant) {
                buildingIdContainer.setVisibility(View.VISIBLE);
                loadProperties();
            } else {
                buildingIdContainer.setVisibility(View.GONE);
            }
        });

        // Building spinner selection listener
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Building" placeholder
                    Property selectedProperty = propertyList.get(position - 1);
                    selectedPropertyId = selectedProperty.getPropId();
                    loadApartmentsForProperty(selectedPropertyId);
                    spinnerApartment.setEnabled(true);
                } else {
                    selectedPropertyId = -1;
                    spinnerApartment.setEnabled(false);
                    apartmentList.clear();
                    ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(
                            SignUpActivity.this,
                            android.R.layout.simple_spinner_item,
                            new String[]{"Select Apartment"}
                    );
                    emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerApartment.setAdapter(emptyAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPropertyId = -1;
            }
        });

        // Apartment spinner selection listener
        spinnerApartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && !apartmentList.isEmpty()) {
                    Apartment selectedApartment = apartmentList.get(position - 1);
                    selectedApartmentId = selectedApartment.getAptId();
                } else {
                    selectedApartmentId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedApartmentId = -1;
            }
        });

        // Sign up logic
        btnSignUp.setOnClickListener(v -> {
            String username = safe(editUsername);
            String email = safe(editEmail);
            String password = safe(editPassword);
            String retype = safe(editRetypePassword);
            String contact = safe(editContact);

            int selectedId = radioGroupRole.getCheckedRadioButtonId();
            RadioButton selectedRoleBtn = findViewById(selectedId);
            String role = selectedRoleBtn != null ? selectedRoleBtn.getText().toString().toUpperCase() : "";

            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retype.isEmpty() || contact.isEmpty()) {
                toast("Please fill all required fields");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                toast("Enter a valid email");
                return;
            }
            if (password.length() < 6) {
                toast("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(retype)) {
                toast("Passwords do not match");
                return;
            }
            if (role.equals("TENANT")) {
                if (selectedPropertyId == -1) {
                    toast("Please select a Building");
                    return;
                }
                if (selectedApartmentId == -1) {
                    toast("Please select an Apartment");
                    return;
                }
            }

            long id;
            if (role.equals("OWNER")) {
                if (landlordDAO.emailExists(email)) {
                    toast("Email already registered (Owner)");
                    return;
                }
                id = landlordDAO.create(username, email, password, contact);
            } else {
                if (tenantDAO.emailExists(email)) {
                    toast("Email already registered (Tenant)");
                    return;
                }
                id = tenantDAO.create(username, email, password, contact,
                        String.valueOf(selectedPropertyId), String.valueOf(selectedApartmentId));
            }

            if (id > 0) {
                toast("Account created! You can log in now.");
                finish();
            } else {
                toast("Sign up failed. Try again.");
            }
        });
    }

    private void loadProperties() {
        propertyList = propertyDAO.getAllProperties();
        List<String> propertyNames = new ArrayList<>();
        propertyNames.add("Select Building"); // Placeholder
        for (Property p : propertyList) {
            propertyNames.add(p.getPropName() + " (ID: " + p.getPropId() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                propertyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuilding.setAdapter(adapter);
    }

    private void loadApartmentsForProperty(long propertyId) {
        apartmentList = apartmentDAO.getAvailableApartmentsByProperty(propertyId);
        List<String> apartmentNames = new ArrayList<>();
        apartmentNames.add("Select Apartment"); // Placeholder

        if (apartmentList.isEmpty()) {
            apartmentNames.add("No available apartments");
        } else {
            for (Apartment a : apartmentList) {
                apartmentNames.add("Apartment " + a.getAptId() + " (Rent: $" + a.getRent() + ")");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                apartmentNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerApartment.setAdapter(adapter);
    }

    private static String safe(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        landlordDAO.close();
        tenantDAO.close();
        propertyDAO.close();
        apartmentDAO.close();
    }
}
