package bca.sendit.filetransfer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bca.sendit.filetransfer.R;

public class NetworkDevicesAdapter extends RecyclerView.Adapter<NetworkDevicesAdapter.DeviceItemHolder> {

    public static class Device {
        public String ipAddress;
        public String hostname;
    }

    private final Context context;
    private final List<Device> devices;

    public NetworkDevicesAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_device_item, parent,
                false);
        return new DeviceItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceItemHolder holder, int position) {
        Device device = devices.get(position);
        holder.ipAddress.setText(device.ipAddress);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceItemHolder extends RecyclerView.ViewHolder {
        TextView ipAddress;

        public DeviceItemHolder(@NonNull View itemView) {
            super(itemView);
            ipAddress = itemView.findViewById(R.id.device_ip_address);
        }
    }
}
