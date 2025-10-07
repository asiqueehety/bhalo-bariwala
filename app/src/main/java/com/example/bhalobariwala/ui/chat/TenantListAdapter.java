package com.example.bhalobariwala.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.R;

import java.util.List;

public class TenantListAdapter extends RecyclerView.Adapter<TenantListAdapter.TenantViewHolder> {

    public static class TenantItem {
        public int tenantId;
        public String tenantName;

        public TenantItem(int tenantId, String tenantName) {
            this.tenantId = tenantId;
            this.tenantName = tenantName;
        }
    }

    private List<TenantItem> tenants;
    private OnTenantClickListener listener;

    public interface OnTenantClickListener {
        void onTenantClick(int tenantId, String tenantName);
    }

    public TenantListAdapter(List<TenantItem> tenants, OnTenantClickListener listener) {
        this.tenants = tenants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TenantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tenant_message, parent, false);
        return new TenantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantViewHolder holder, int position) {
        TenantItem tenant = tenants.get(position);
        holder.bind(tenant, listener);
    }

    @Override
    public int getItemCount() {
        return tenants != null ? tenants.size() : 0;
    }

    static class TenantViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenantName, tvLastMessage;

        public TenantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }

        public void bind(TenantItem tenant, OnTenantClickListener listener) {
            tvTenantName.setText(tenant.tenantName);
            tvLastMessage.setText("Tap to view conversation");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTenantClick(tenant.tenantId, tenant.tenantName);
                }
            });
        }
    }
}

