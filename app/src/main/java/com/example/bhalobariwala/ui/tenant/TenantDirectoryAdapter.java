package com.example.bhalobariwala.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhalobariwala.R;

import java.util.List;

public class TenantDirectoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_BUILDING = 0;
    private static final int VIEW_TYPE_TENANT = 1;

    private BuildingInfo buildingInfo;
    private List<TenantInfo> tenantList;

    public TenantDirectoryAdapter(BuildingInfo buildingInfo, List<TenantInfo> tenantList) {
        this.buildingInfo = buildingInfo;
        this.tenantList = tenantList;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_BUILDING : VIEW_TYPE_TENANT;
    }

    @Override
    public int getItemCount() {
        return 1 + (tenantList != null ? tenantList.size() : 0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BUILDING) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_building_info, parent, false);
            return new BuildingViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tenant_info, parent, false);
            return new TenantViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BuildingViewHolder && buildingInfo != null) {
            ((BuildingViewHolder) holder).bind(buildingInfo);
        } else if (holder instanceof TenantViewHolder && tenantList != null) {
            TenantInfo tenant = tenantList.get(position - 1);
            ((TenantViewHolder) holder).bind(tenant);
        }
    }

    // ViewHolder for Building Info
    static class BuildingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuildingName, tvBuildingId, tvLandlordName, tvLandlordPhone;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBuildingName = itemView.findViewById(R.id.tvBuildingName);
            tvBuildingId = itemView.findViewById(R.id.tvBuildingId);
            tvLandlordName = itemView.findViewById(R.id.tvLandlordName);
            tvLandlordPhone = itemView.findViewById(R.id.tvLandlordPhone);
        }

        public void bind(BuildingInfo info) {
            tvBuildingName.setText(info.getBuildingName());
            tvBuildingId.setText(String.valueOf(info.getBuildingId()));
            tvLandlordName.setText(info.getLandlordName());
            tvLandlordPhone.setText(info.getLandlordPhone());
        }
    }

    // ViewHolder for Tenant Info
    static class TenantViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenantName, tvTenantContact, tvAptId;

        public TenantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvTenantContact = itemView.findViewById(R.id.tvTenantContact);
            tvAptId = itemView.findViewById(R.id.tvAptId);
        }

        public void bind(TenantInfo tenant) {
            tvTenantName.setText(tenant.getName());
            tvTenantContact.setText(tenant.getContact());
            tvAptId.setText("Apartment ID: " + tenant.getAptId());
        }
    }

    public void updateData(BuildingInfo buildingInfo, List<TenantInfo> tenantList) {
        this.buildingInfo = buildingInfo;
        this.tenantList = tenantList;
        notifyDataSetChanged();
    }
}

