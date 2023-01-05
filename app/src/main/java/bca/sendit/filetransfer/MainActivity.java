package bca.sendit.filetransfer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.adapters.DevicesAdapter;
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
        App.setToolbarTransparent(this, getSupportActionBar());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // For now set false to ignore onboard
        boolean isFirstLaunch = sharedPreferences.getBoolean("first_launch", false);

        if (isFirstLaunch) {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
            return;
        }


        setContentView(R.layout.activity_main);

        watchAuthDevices();

        List<Path> paths = new ArrayList<>();
        paths.add(new Path("/", new HomeView()));
        paths.add(new Path("/api/photos/", new PhotosApiView()));
        paths.add(new Path("/api/videos/", new VideosApiView()));
        paths.add(new Path("/test/", new HomeView()));

        PathMatcher pathMatcher = new PathMatcher(paths);
        try {
            Configuration configuration = new Configuration();
            configuration.isPrivateMode = true;
            HttpServer httpServer = new HttpServer(this, configuration, pathMatcher, 8080);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0);

        ServerEventsReceiver serverEventsReceiver = new ServerEventsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Events.ACTION_REQUEST_ALLOW);
        registerReceiver(serverEventsReceiver, intentFilter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void watchAuthDevices() {
        // Prepare recycler view
        List<AuthToken> authTokenList = new ArrayList<>();
        RecyclerView authsDeviceView = findViewById(R.id.auths_device_recycler_view);
        authsDeviceView.setLayoutManager(new LinearLayoutManager(this));
        DevicesAdapter devicesAdapter = new DevicesAdapter(this, authTokenList);
        authsDeviceView.setAdapter(devicesAdapter);

        DbManager.get(this).authsTokenDao().getTokens().observe(this, authTokens -> {
            authTokenList.clear();
            authTokenList.addAll(authTokens);
            devicesAdapter.notifyDataSetChanged();
        });
    }


    private void requestDialog(String ipAddress, String token) {
        // If any previous request dialog exist, cancel it
        if (requestAccessDialog != null) {
            requestAccessDialog.cancel();
        }

        MaterialAlertDialogBuilder requestDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        requestDialogBuilder.setTitle(ipAddress + " is requesting...");
        requestDialogBuilder.setMessage("Allow this device to access your shared file?\nID: " + token);
        requestDialogBuilder.setNegativeButton("Deny", (dialogInterface, i) -> dialogInterface.dismiss());
        requestDialogBuilder.setPositiveButton("Allow", (dialogInterface, i) ->
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