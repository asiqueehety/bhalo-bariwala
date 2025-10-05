package com.example.bhalobariwala.ui.owner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.R;
import com.example.bhalobariwala.model.Property;

import java.util.List;

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.VH> {

    private List<Property> items;

    public PropertiesAdapter(List<Property> items) {
        this.items = items;
    }

    public void setItems(List<Property> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Property p = items.get(pos);
        h.name.setText(p.getPropName());
        h.id.setText("ID: " + p.getPropId()); // prop_id under name
    }

    @Override
    public int getItemCount() { return items == null ? 0 : items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, id;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvPropName);
            id   = itemView.findViewById(R.id.tvPropId);
        }
    }
}
