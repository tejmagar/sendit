package bca.sendit.filetransfer.server;

import android.content.Context;

import fi.iki.elonen.NanoHTTPD;

public class ResponseTemplate {
    public static NanoHTTPD.Response fileNotFound(Context context) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.NOT_FOUND,
                "text/html", AssetsHandler.getInputStream(context, "404.html"));
    }

    public static NanoHTTPD.Response internalError(Context context) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                "text/html", AssetsHandler.getInputStream(context, "500.html"));
    }

    public static NanoHTTPD.Response forbidden(Context context, String text) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.FORBIDDEN,
                "text/plain", text);
    }
}
