package bca.sendit.filetransfer.server;

import static fi.iki.elonen.NanoHTTPD.Response.Status;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import bca.sendit.filetransfer.paths.PathMatcher;
import bca.sendit.filetransfer.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    public static final String TAG = "HttpServer";

    private final Context context;
    private final PathMatcher pathMatcher;

    public HttpServer(Context context, int port, PathMatcher pathMatcher) {
        super(port);
        this.context = context;
        this.pathMatcher = pathMatcher;
    }

    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
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

            // Handle API responses
            ResponseView responseView = pathMatcher.getMatchedView(session);

            if (responseView == null) {
                // No response view found to handle request, probably because no path was matched
                return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, "text/html",
                        "404 Error: File not found");
            }

            return responseView.getResponse(context, session);
        } catch (Exception e) {
            e.printStackTrace();

            // Handle if server crashed while processing
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, "text/html",
                    "500 Error: Internal server error");
        }
    }
}