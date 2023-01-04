package bca.sendit.filetransfer.server;

import static fi.iki.elonen.NanoHTTPD.Response.Status;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import bca.sendit.filetransfer.server.auths.Auths;
import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    public static final String TAG = "HttpServer";

    private final Context context;
    private final PathMatcher pathMatcher;
    private final Configuration configuration;

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
     * Prepare request object with necessary information for passing to class extending ResponseView
     *
     * @param session   IHTTPSession
     * @param authToken authToken
     * @return request
     */
    private Request getRequest(IHTTPSession session, String authToken) {
        Request request = new Request();
        if (authToken == null) {
            authToken = Auths.generateToken();
        }
        request.setAuthToken(authToken);
        request.setSession(session);
        request.setAuthenticated(Auths.isAuthenticated(context, authToken));
        request.setConfiguration(configuration);
        return request;
    }


    /**
     * Responsible for handling request
     *
     * @param session IHHTPSession
     * @return response
     */
    @Override
    public Response serve(IHTTPSession session) {
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
                return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, "text/html",
                        "404 Error: File not found");
            }

            CookieHandler cookieHandler = new CookieHandler(session.getHeaders());
            String authToken = cookieHandler.read("Authorization: Token");
            Request request = getRequest(session, authToken);
            Response response = responseView.getResponse(context, request);

            if (authToken == null) {
                // Looks like device is requesting for the first time or cookie has been cleared
                cookieHandler.set("Authorization: Token", request.getAuthToken(), 7 * 86400);
                cookieHandler.unloadQueue(response);
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();

            // Handle if server crashed while processing
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, "text/html",
                    "500 Error: Internal server error");
        }
    }
}