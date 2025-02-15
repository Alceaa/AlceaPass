package com.alcea.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alcea.R;
import com.alcea.models.Service;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {
    private final List<Service> servicesList;

    public ServicesAdapter(List<Service> servicesList) {
        this.servicesList = servicesList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = servicesList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceTimestamp.setText(service.getTimestamp());

        if (service.getLogoResId() != null) {
            holder.serviceLogo.setImageResource(service.getLogoResId());
            holder.serviceLogo.setVisibility(View.VISIBLE);
        } else {
            holder.serviceLogo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        ImageView serviceLogo;
        TextView serviceTimestamp;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceLogo = itemView.findViewById(R.id.service_logo);
            serviceTimestamp = itemView.findViewById(R.id.service_timestamp);
        }
    }
}
