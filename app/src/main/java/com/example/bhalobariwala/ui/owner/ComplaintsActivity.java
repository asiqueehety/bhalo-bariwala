package com.example.bhalobariwala.ui.owner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.DatabaseHelper;
import com.example.bhalobariwala.R;
import com.example.bhalobariwala.SessionManager;
import com.example.bhalobariwala.ui.owner.ComplaintAdapter;
import com.example.bhalobariwala.ui.owner.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComplaintAdapter adapter;
    private List<Complaint> complaintList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        recyclerView = findViewById(R.id.recyclerComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        complaintList = fetchComplaints();

        adapter = new ComplaintAdapter(complaintList);
        recyclerView.setAdapter(adapter);
    }

    private List<Complaint> fetchComplaints() {
        List<Complaint> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get logged in owner id
        SessionManager session = new SessionManager(this);
        int ownerId = session.getUserId();   // we stored this in LoginActivity

        String query =
                "SELECT c." + DatabaseHelper.C_TITLE + ", " +
                        "c." + DatabaseHelper.C_DESC + ", " +
                        "c." + DatabaseHelper.C_TYPE + ", " +
                        "t." + DatabaseHelper.T_PROP_ID + ", " +
                        "t." + DatabaseHelper.T_APT_ID + ", " +
                        "p." + DatabaseHelper.P_NAME +
                        " FROM " + DatabaseHelper.T_COMPLAINTS + " c " +
                        " JOIN " + DatabaseHelper.T_TENANT + " t ON c." + DatabaseHelper.C_TID + " = t." + DatabaseHelper.T_ID +
                        " JOIN " + DatabaseHelper.T_PROPERTY + " p ON t." + DatabaseHelper.T_PROP_ID + " = p." + DatabaseHelper.P_ID +
                        " WHERE p." + DatabaseHelper.P_LANDLORDID + " = ?";   // filter by logged-in owner

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ownerId)});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String desc = cursor.getString(1);
                String type = cursor.getString(2);
                int propId = cursor.getInt(3);
                int aptId = cursor.getInt(4);
                String propName = cursor.getString(5);

                list.add(new Complaint(title, desc, propId, propName, aptId, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return list;
    }

}
