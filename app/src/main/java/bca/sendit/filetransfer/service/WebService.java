package bca.sendit.filetransfer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.Configuration;
import bca.sendit.filetransfer.server.Events;
import bca.sendit.filetransfer.server.HttpServer;
import bca.sendit.filetransfer.server.OnServerEvent;
import bca.sendit.filetransfer.server.SingletonHttpServer;
import bca.sendit.filetransfer.server.WebSocketServer;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.paths.Path;
import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.views.FileDownloadView;
import bca.sendit.filetransfer.server.views.FileUploadsView;
import bca.sendit.filetransfer.server.views.HomeView;
import bca.sendit.filetransfer.server.views.PhotosApiView;
import bca.sendit.filetransfer.server.views.VideosApiView;

public class WebService extends Service {

    private static final String TAG = "WebService";
    private HttpServer httpServer;

    private final List<WebSocketServer> webSocketServers = new ArrayList<>();
    private ServerEventsReceiver eventsReceiver;
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<Path> createRoutes() {
        List<Path> paths = new ArrayList<>();
        paths.add(new Path("/", new HomeView()));
        paths.add(new Path("/api/photos/", new PhotosApiView()));
        paths.add(new Path("/api/videos/", new VideosApiView()));
        paths.add(new Path("/api/uploads/", new FileUploadsView()));
        paths.add(new Path("/test/", new HomeView()));
        paths.add(new Path("/download/", new FileDownloadView()));
        return paths;
    }

    private HttpServer createHttpServer(boolean isServePrivate) {
        // Add your paths in Routes class
        List<Path> paths = createRoutes();
        PathMatcher pathMatcher = new PathMatcher(paths);
        Configuration configuration = new Configuration();
        configuration.isPrivateMode = isServePrivate;
        httpServer = new HttpServer(this, configuration, pathMatcher, 8080);
        SingletonHttpServer.init(httpServer);
        return SingletonHttpServer.getInstance().getHttpServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        boolean isServePrivate = intent.getBooleanExtra(Configuration.SERVE_MODE_PRIVATE, false);
        httpServer = createHttpServer(isServePrivate);
        httpServer.attachEventListener(new OnServerEvent() {
            @Override
            public void onOpenWebSocket(WebSocketServer webSocketServer) {
                webSocketServers.add(webSocketServer);
            }

            @Override
            public void onCloseWebSocket(WebSocketServer webSocketServer) {
                webSocketServers.remove(webSocketServer);
            }
        });

        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            stopService(intent);
        }

        FileUploadSession.setCallback(path -> handleUpload());
        
        eventsReceiver = new ServerEventsReceiver();
        IntentFilter intentFilter = new IntentFilter(Events.ACTION_REQUEST_RESULT);
        registerReceiver(eventsReceiver, intentFilter);
        return START_STICKY;
    }

    private void handleUpload() {
        new Thread(() -> {
            for (WebSocketServer webSocketServer : webSocketServers) {
                if (webSocketServer.isOpen()) {
                    // Send message through websocket to active clients
                    try {
                        JSONObject message = new JSONObject();
                        message.put("type", "uploads");
                        webSocketServer.send(message.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendAllowedPermission(String token, boolean isAllowed) {
        new Thread(() -> {
            for (WebSocketServer webSocketServer : webSocketServers) {
                boolean pathMatched = webSocketServer.getHandshakeRequest().getUri()
                        .equals("/websocket/" + token + "/");
                if (pathMatched && webSocketServer.isOpen()) {

                    // Send message through websocket to active clients
                    try {
                        JSONObject message = new JSONObject();
                        message.put("type", "permission");
                        message.put("value", isAllowed);
                        webSocketServer.send(message.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if (httpServer != null) {
            httpServer.stop();
        }

        if (eventsReceiver != null) {
            unregisterReceiver(eventsReceiver);
        }

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    class ServerEventsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Events.ACTION_REQUEST_RESULT)) {
                String authToken = intent.getStringExtra(Events.AUTH_TOKEN);
                boolean result = intent.getBooleanExtra(Events.REQUEST_RESULT, false);
                sendAllowedPermission(authToken, result);
            }
        }
    }

}
