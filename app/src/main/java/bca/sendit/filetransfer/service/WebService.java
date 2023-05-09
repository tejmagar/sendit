package bca.sendit.filetransfer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;

import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.tinyweb.TinyWebWS;
import org.tinyweb.conf.Configurations;

import java.io.IOException;

import bca.sendit.filetransfer.MainActivity;
import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.server.Server;

public class WebService extends Service {

    private static final String TAG = "WebService";
    private static final int FOREGROUND_ID = 1234;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.app_name),
                    "File server", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.app_name));
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText("File sharing is turned on...");
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        createNotificationChannel();
        startForeground(FOREGROUND_ID, buildNotification());

        if (intent != null && intent.getBooleanExtra("start", false)) {
            startServer(intent);
        } else {
            stopSelf();
        }

        return START_STICKY;
    }

    private void setConfigurations(TinyWebWS tinyWebWS) {
        Configurations configurations = new Configurations();
        configurations.get().put("context", getApplicationContext());
        tinyWebWS.setConfigurations(configurations);
    }

    private void startServer(Intent intent) {
        int port = intent.getIntExtra("port", 8080);
        Server.setPort(port);
        TinyWebWS tinyWebWS = Server.getInstance();
        setConfigurations(tinyWebWS);

        try {
            tinyWebWS.start();
        } catch (IOException e) {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Server.getInstance().stop();
        Log.d(TAG, "onDestroy");
    }
}
