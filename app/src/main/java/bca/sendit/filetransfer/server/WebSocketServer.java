package bca.sendit.filetransfer.server;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class WebSocketServer extends NanoWSD.WebSocket {
    public static final String TAG = "WebSocketServer";

    private final OnServerEvent onServerEvent;
    private Timer timer;

    public WebSocketServer(NanoHTTPD.IHTTPSession handshakeRequest, OnServerEvent onServerEvent) {
        super(handshakeRequest);
        this.onServerEvent = onServerEvent;
    }

    @Override
    protected void onOpen() {
        Log.d(TAG, "Connection opened");
        onServerEvent.onOpenWebSocket(this);

        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    ping("ping".getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Send a ping message every 3 seconds
        timer.schedule(timerTask, 0, 3000);
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pongFrame) {
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame messageFrame) {
        Log.d(TAG, "Message: " + messageFrame.getTextPayload());
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason,
                           boolean initiatedByRemote) {
        onServerEvent.onCloseWebSocket(this);

        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onException(IOException e) {
        e.printStackTrace();

        if (timer != null) {
            timer.cancel();
        }
    }
}