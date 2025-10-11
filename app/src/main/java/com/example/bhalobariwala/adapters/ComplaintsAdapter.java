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

    private final Context ctx;
    private final List<Map<String, String>> data;

    public ComplaintsAdapter(Context ctx, List<Map<String, String>> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder h, int position) {
        Map<String, String> row = data.get(position);

        String title   = val(row.get("title"));
        String desc    = val(row.get("desc"));
        String type    = val(row.get("type"));
        String propId  = val(row.get("prop_id"));
        String aptId   = val(row.get("apt_id"));

        h.tvTitle.setText(title);
        h.tvDesc.setText(desc.isEmpty() ? "No description" : desc);
        h.tvType.setText("Type: " + (type.isEmpty() ? "-" : type));
        h.tvProperty.setText("Property: " + (propId.isEmpty() ? "-" : propId));
        h.tvApartment.setText("Apartment: " + (aptId.isEmpty() ? "-" : aptId));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static String val(String s) { return s == null ? "" : s; }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvProperty, tvApartment, tvType;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle     = itemView.findViewById(R.id.tvTitle);
            tvDesc      = itemView.findViewById(R.id.tvDesc);
            tvProperty  = itemView.findViewById(R.id.tvProperty);
            tvApartment = itemView.findViewById(R.id.tvApartment);
            tvType      = itemView.findViewById(R.id.tvType);
        }
    }
}
