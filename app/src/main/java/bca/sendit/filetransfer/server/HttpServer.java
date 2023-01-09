package bca.sendit.filetransfer.server;

import static fi.iki.elonen.NanoHTTPD.Response.Status;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.paths.ResponseView;

import bca.sendit.filetransfer.server.request.Request;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;


public class HttpServer extends NanoWSD {

    public static final String TAG = "HttpServer";

    private final Context context;
    private final PathMatcher pathMatcher;
    private final Configuration configuration;
    private OnServerEvent onServerEvent;

    public HttpServer(Context context, Configuration configuration, PathMatcher pathMatcher, int port) {
        super(port);
        this.configuration = configuration;
        this.context = context;
        this.pathMatcher = pathMatcher;
    }

    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    /**
     * Call this method if you need server events such as <br><code>onWebSocketOpen(WebSocketServer webSocket)</code>.
     * Without calling this method, websocket won't work. This method consist interface for WebSocket events.
     * @param onServerEvent OnAttachHandler
     */
    public void attachEventListener(OnServerEvent onServerEvent) {
        this.onServerEvent = onServerEvent;
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        Log.d(TAG, "Current path: " + handshake.getUri());

        //Currently hardcoded path
        if (handshake.getUri().startsWith("/websocket/")) {
            return new WebSocketServer(handshake, onServerEvent);
        }

        return null;
    }

    /**
     * Responsible for handling request
     *
     * @param session IHHTPSession
     * @return response
     */
    @Override
    public Response serveHttp(IHTTPSession session) {
        Log.d(TAG, "Current path: " + session.getUri());

        try {
            // Try checking assets directory first for html files and stylesheets. If found, serve it.

            InputStream inputStream = AssetsHandler.getInputStream(context, session.getUri());
            if (inputStream != null) {
                String mimeType = Utils.getMimeType(session.getUri());

                if (mimeType == null) {
                    // Default mime type for directly downloading file in browser
                    mimeType = "application/octet-stream";
                }

                return newChunkedResponse(Status.OK, mimeType, inputStream);
            }

            // Handle Views response
            ResponseView responseView = pathMatcher.getMatchedView(session);

            if (responseView == null) {
                // No response view found to handle request, probably because no path was matched
                return ResponseTemplate.fileNotFound(context);
            }

            CookieHandler cookieHandler = new CookieHandler(session.getHeaders());
            String authToken = Request.getAuthToken(cookieHandler);
            Request request = Request.build(context, session, authToken, configuration);
            Response response = responseView.getResponse(context, request);

            if (authToken == null) {
                // Looks like device is requesting for the first time or cookie has been cleared
                cookieHandler.set("Authorization: Token",
                        request.getAuthToken() + "; SameSite=Lax", 7 * 86400);
                cookieHandler.unloadQueue(response);
            }

            // Add this header to allow from any remote host to send request
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        } catch (Exception e) {
            e.printStackTrace();

            // Handle if server crashed while processing
            return ResponseTemplate.internalError(context);
        }
    }
}