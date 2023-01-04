package bca.sendit.filetransfer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.Configuration;
import bca.sendit.filetransfer.server.Events;
import bca.sendit.filetransfer.server.auths.AuthToken;
import bca.sendit.filetransfer.server.auths.Auths;
import bca.sendit.filetransfer.server.paths.Path;
import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.HttpServer;
import bca.sendit.filetransfer.server.views.HomeView;
import bca.sendit.filetransfer.server.views.PhotosApiView;
import bca.sendit.filetransfer.server.views.VideosApiView;

public class MainActivity extends AppCompatActivity {

    AlertDialog requestAccessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Path> paths = new ArrayList<>();
        paths.add(new Path("/", new HomeView()));
        paths.add(new Path("/api/photos/", new PhotosApiView()));
        paths.add(new Path("/api/videos/", new VideosApiView()));
        paths.add(new Path("/test/", new HomeView()));

        PathMatcher pathMatcher = new PathMatcher(paths);
        try {
            Configuration configuration = new Configuration();
            configuration.isPrivateMode = false;
            HttpServer httpServer = new HttpServer(this, configuration, pathMatcher, 8080);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DbManager.get(this).authsTokenDao().getTokens().observe(this, new Observer<List<AuthToken>>() {
            @Override
            public void onChanged(List<AuthToken> authTokens) {
//                for (AuthToken authToken : authTokens) {
//                    Toast.makeText(MainActivity.this, authToken.token, Toast.LENGTH_SHORT).show();
//                }
            }
        });
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0);


        ServerEventsReceiver serverEventsReceiver = new ServerEventsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Events.ACTION_REQUEST_ALLOW);
        registerReceiver(serverEventsReceiver, intentFilter);
    }

    private void requestDialog(String ipAddress, String token) {
        // If any previous request dialog exist, cancel it
        if (requestAccessDialog != null) {
            requestAccessDialog.cancel();
        }

        MaterialAlertDialogBuilder requestDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        requestDialogBuilder.setTitle(ipAddress + " is requesting...");
        requestDialogBuilder.setMessage("Allow this device to access your shared file?\nID: " + token);
        requestDialogBuilder.setNegativeButton("Don't Allow", (dialogInterface, i) -> dialogInterface.dismiss());

        requestDialogBuilder.setPositiveButton("Allow Access", (dialogInterface, i) ->
                new Thread(() -> {
                    AuthToken authToken = new AuthToken(token, true);
                    Auths.saveToken(MainActivity.this, authToken);

                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Allowed access to " + ipAddress, Toast.LENGTH_SHORT).show());
                }).start());

        requestDialogBuilder.setCancelable(false);
        requestAccessDialog = requestDialogBuilder.show();
    }

    class ServerEventsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Events.ACTION_REQUEST_ALLOW)) {
                String ipAddress = intent.getStringExtra(Events.REMOTE_IP_ADDRESS);
                String authToken = intent.getStringExtra(Events.AUTH_TOKEN);
                requestDialog(ipAddress, authToken);
                Toast.makeText(context, "Incoming request...", Toast.LENGTH_SHORT).show();
            }
        }
    }

}