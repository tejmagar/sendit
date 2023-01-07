package bca.sendit.filetransfer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bca.sendit.filetransfer.server.Configuration;
import bca.sendit.filetransfer.server.HttpServer;
import bca.sendit.filetransfer.server.OnServerEvent;
import bca.sendit.filetransfer.server.WebSocketServer;
import bca.sendit.filetransfer.server.paths.Path;
import bca.sendit.filetransfer.server.paths.PathMatcher;

public class WebService extends Service {

    private HttpServer httpServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Add your paths in Routes class
        List<Path> paths = Routes.getPaths();
        PathMatcher pathMatcher = new PathMatcher(paths);

        boolean isServePrivate = intent.getBooleanExtra(Configuration.SERVE_MODE_PRIVATE, true);

        Configuration configuration = new Configuration();
        configuration.isPrivateMode = isServePrivate;

        httpServer = new HttpServer(this, configuration, pathMatcher, 8080);
        httpServer.attachEventListener(new OnServerEvent() {
            @Override
            public void onOpenWebSocket(WebSocketServer webSocketServer) {


            }

            @Override
            public void onCloseWebSocket(WebSocketServer webSocketServer) {

            }
        });

        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            stopService(intent);
        }
        Log.d("started", "yes");
        Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        httpServer.stop();
        Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
    }
}
