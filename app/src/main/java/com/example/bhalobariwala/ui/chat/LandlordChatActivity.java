package com.example.bhalobariwala.ui.chat;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.DatabaseHelper;
import com.example.bhalobariwala.MessageDAO;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;
import com.example.bhalobariwala.model.Message;

import java.util.ArrayList;
import java.util.List;

public class LandlordChatActivity extends AppCompatActivity {

    private TextView tvTenantName;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private MessageAdapter adapter;
    private MessageDAO messageDAO;
    private SessionManager sessionManager;

    private int landlordId;
    private int tenantId;
    private String tenantName;

    private Handler handler = new Handler();
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_chat);

        // Initialize views
        tvTenantName = findViewById(R.id.tvTenantName);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Setup RecyclerView
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

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

        // Get tenant info from intent
        tenantId = getIntent().getIntExtra("TENANT_ID", -1);
        tenantName = getIntent().getStringExtra("TENANT_NAME");

        if (tenantId == -1) {
            Toast.makeText(this, "Invalid tenant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTenantName.setText("Chat with " + tenantName);

        // Load messages
        loadMessages();

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Start polling
        startPolling();
    }

    private void loadMessages() {
        messageDAO.open();

        List<Message> messages = new ArrayList<>();
        Cursor cursor = messageDAO.getConversation(landlordId, tenantId);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.M_ID));
                int landlord = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.M_LANDLORD_ID));
                int tenant = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.M_TENANT_ID));
                String senderType = cursor.getString(cursor.getColumnIndex(DatabaseHelper.M_SENDER_TYPE));
                String msg = cursor.getString(cursor.getColumnIndex(DatabaseHelper.M_MESSAGE));
                long timestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.M_TIMESTAMP));
                boolean isRead = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.M_IS_READ)) == 1;

                messages.add(new Message(id, landlord, tenant, senderType, msg, timestamp, isRead));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Mark messages as read
        messageDAO.markAsRead(landlordId, tenantId, "landlord");
        messageDAO.close();

        // Setup adapter
        if (adapter == null) {
            adapter = new MessageAdapter(messages, "landlord");
            rvMessages.setAdapter(adapter);
        } else {
            adapter.updateMessages(messages);
        }

        // Scroll to bottom
        if (messages.size() > 0) {
            rvMessages.scrollToPosition(messages.size() - 1);
        }
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        messageDAO.open();
        long result = messageDAO.sendMessage(landlordId, tenantId, "landlord", message);
        messageDAO.close();

        if (result != -1) {
            etMessage.setText("");
            loadMessages();
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPolling() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 3000); // Poll every 3 seconds
            }
        };
        handler.postDelayed(pollingRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
        if (messageDAO != null) {
            messageDAO.close();
        }
    }
}

