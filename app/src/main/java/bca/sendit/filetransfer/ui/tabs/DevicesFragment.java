package bca.sendit.filetransfer.ui.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.server.UserSession;
import bca.sendit.filetransfer.ui.adapters.NetworkDevicesAdapter;


public class DevicesFragment extends Fragment {
    private final List<NetworkDevicesAdapter.Device> devices = new ArrayList<>();
    private NetworkDevicesAdapter networkDevicesAdapter;
    private UserSession.OnNewDeviceConnectedListener onNewDeviceConnectedListener;

    private TextView noDeviceMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        noDeviceMessage = view.findViewById(R.id.no_devices_message);

        RecyclerView recyclerView = view.findViewById(R.id.online_devices_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        networkDevicesAdapter = new NetworkDevicesAdapter(getContext(), devices);
        recyclerView.setAdapter(networkDevicesAdapter);

        onNewDeviceConnectedListener = (ipAddress, hostname) -> {
            devices.add(createDeviceItem(ipAddress, hostname));

            new Handler(Looper.getMainLooper()).post(() -> {
                hideNoDeviceMessage();
                networkDevicesAdapter.notifyItemChanged(devices.size() - 1);
            });
        };

        UserSession.attachListener(onNewDeviceConnectedListener);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();

        // Add devices and notify adapter
        devices.clear();
        addDevices();
        networkDevicesAdapter.notifyDataSetChanged();

        // Hide message if there are devices
        if (UserSession.getDevicesInfo().size() > 0) {
            hideNoDeviceMessage();
        }
    }

    private void hideNoDeviceMessage() {
        noDeviceMessage.setVisibility(View.GONE);
    }

    private NetworkDevicesAdapter.Device createDeviceItem(String ipAddress, String hostname) {
        NetworkDevicesAdapter.Device device = new NetworkDevicesAdapter.Device();
        device.ipAddress = ipAddress;
        device.hostname = hostname;
        return device;
    }

    private void addDevices() {
        Map<String, String> ipAndHostnameMap = UserSession.getDevicesInfo();
        for (String ipAddress : ipAndHostnameMap.keySet()) {
            String hostname = ipAndHostnameMap.get(ipAddress);
            devices.add(createDeviceItem(hostname, ipAddress));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserSession.deAttachListener(onNewDeviceConnectedListener);
    }
}
