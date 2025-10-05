package com.example.bhalobariwala.ui.owner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhalobariwala.LandlordDAO;
import com.example.bhalobariwala.R;
import com.google.android.material.textfield.TextInputEditText;

public class OwnerProfileActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView txtName, txtEmail, txtContact;
    private Button btnEditProfile, btnSaveChanges;
    private View editContainer;
    private TextInputEditText editName, editEmail, editContact;

    private LandlordDAO landlordDAO;
    private long landlordId = 1; // demo: assume logged-in landlord has ID = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        imageProfile = findViewById(R.id.imageProfile);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtContact = findViewById(R.id.txtContact);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        editContainer = findViewById(R.id.editContainer);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editContact = findViewById(R.id.editContact);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        landlordDAO = new LandlordDAO(this);
        landlordDAO.open();

        // Fetch data
        Landlord landlord = landlordDAO.getLandlordById(landlordId);
        if (landlord != null) {
            txtName.setText(landlord.getName());
            txtEmail.setText(landlord.getEmail());
            txtContact.setText(landlord.getContact());
        }

        // Edit button toggles edit mode
        btnEditProfile.setOnClickListener(v -> {
            editContainer.setVisibility(View.VISIBLE);
            editName.setText(txtName.getText());
            editEmail.setText(txtEmail.getText());
            editContact.setText(txtContact.getText());
        });

        btnSaveChanges.setOnClickListener(v -> {
            String newName = safe(editName);
            String newEmail = safe(editEmail);
            String newContact = safe(editContact);

            if (newName.isEmpty() || newEmail.isEmpty() || newContact.isEmpty()) {
                toast("All fields required");
                return;
            }

            boolean updated = landlordDAO.updateLandlord(landlordId, newName, newEmail, newContact);
            if (updated) {
                txtName.setText(newName);
                txtEmail.setText(newEmail);
                txtContact.setText(newContact);
                toast("Profile updated!");
                editContainer.setVisibility(View.GONE);
            } else {
                toast("Update failed");
            }
        });
    }

    private String safe(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        landlordDAO.close();
    }

    // Inner class (you can make it a separate file)
    public static class Landlord {
        private final long id;
        private final String name;
        private final String email;
        private final String contact;

        public Landlord(long id, String name, String email, String contact) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.contact = contact;
        }
        public long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getContact() { return contact; }
    }
}
