package bca.sendit.filetransfer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSession {
    public interface OnNewDeviceConnectedListener {
        void connected(String ipAddress, String hostname);
    }

    private static final Map<String, String> ipAndHostnameMap = new HashMap<>();

    private static final List<OnNewDeviceConnectedListener> onNewDeviceConnectedListeners = new ArrayList<>();

    public static void attachListener(OnNewDeviceConnectedListener onNewDeviceConnectedListener) {
        onNewDeviceConnectedListeners.add(onNewDeviceConnectedListener);
    }

    public static void deAttachListener(OnNewDeviceConnectedListener onNewDeviceConnectedListener) {
        onNewDeviceConnectedListeners.remove(onNewDeviceConnectedListener);
    }

    /**
     * Returns IP Address and hostname pair
     *
     * @return ipAndHostnameMap
     */
    public static Map<String, String> getDevicesInfo() {
        return ipAndHostnameMap;
    }

    public static void addDeviceInfo(String ipAddress, String hostname) {
        if (ipAndHostnameMap.containsKey(ipAddress)) {
            return;
        }

        ipAndHostnameMap.put(ipAddress, hostname);

        for (OnNewDeviceConnectedListener onNewDeviceConnectedListener : onNewDeviceConnectedListeners) {
            onNewDeviceConnectedListener.connected(ipAddress, hostname);
        }
    }

}
