package com.example.bhalobariwala.ui.owner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.R;
import com.example.bhalobariwala.ui.owner.Complaint;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private List<Complaint> complaintList;

    public ComplaintAdapter(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvDesc.setText(complaint.getDescription());
        holder.tvProperty.setText("Property ID: " + complaint.getPropertyId() + " | " + complaint.getPropertyName());
        holder.tvApartment.setText("Apartment ID: " + complaint.getApartmentId());
        holder.tvType.setText("Type: " + complaint.getType());
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
