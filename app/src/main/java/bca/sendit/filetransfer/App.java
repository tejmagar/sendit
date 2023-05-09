package bca.sendit.filetransfer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import bca.sendit.filetransfer.service.WebService;

public class App {
    public static void setToolbarTransparent(Context context, ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                    android.R.color.transparent));
            actionBar.setElevation(0);
        }
    }

    public static boolean isNotificationRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WebService.class.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> getServerPort(int port) {
        List<String> ipAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ipAddresses.add(inetAddress.getHostAddress() + ":" + port);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ipAddresses;
    }

    public static void setServerPort(Context context, int port) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("port", port).apply();
        editor.apply();
    }

    public static int getServerPort(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("port", 8080);
    }
}
