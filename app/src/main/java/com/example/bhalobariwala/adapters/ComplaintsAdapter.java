package com.example.bhalobariwala.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.R;

import java.util.List;
import java.util.Map;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintViewHolder> {

    private final Context context;
    private final List<Map<String, String>> complaintList; // changed type

    public ComplaintsAdapter(Context context, List<Map<String, String>> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Map<String, String> complaint = complaintList.get(position);

        // safely get values from the map
        String title = complaint.get("title");
        String desc = complaint.get("desc");
        String type = complaint.get("type");
        String property = complaint.get("property_name"); // if exists
        String aptId = complaint.get("apt_id");

        holder.tvTitle.setText(title != null ? title : "");
        holder.tvDesc.setText(desc != null ? desc : "");
        holder.tvType.setText("Type: " + (type != null ? type : ""));
        holder.tvProperty.setText("Property: " + (property != null ? property : "N/A"));
        holder.tvApartment.setText("Apartment: " + (aptId != null ? aptId : "N/A"));
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvProperty, tvApartment, tvType;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvProperty = itemView.findViewById(R.id.tvProperty);
            tvApartment = itemView.findViewById(R.id.tvApartment);
            tvType = itemView.findViewById(R.id.tvType);
        }
    }
}
