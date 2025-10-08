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

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {
    private final Context context;
    private final List<Map<String, String>> complaints;

    public ComplaintsAdapter(Context context, List<Map<String, String>> complaints) {
        this.context = context;
        this.complaints = complaints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> c = complaints.get(position);
        holder.title.setText(c.get("title"));
        holder.type.setText("Type: " + c.get("type"));
        holder.desc.setText(c.get("desc"));
        holder.aptId.setText("Apartment ID: " + c.get("apt_id"));
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, type, desc, aptId;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.c_title);
            type = itemView.findViewById(R.id.c_type);
            desc = itemView.findViewById(R.id.c_desc);
            aptId = itemView.findViewById(R.id.c_apt_id);
        }
    }
}
