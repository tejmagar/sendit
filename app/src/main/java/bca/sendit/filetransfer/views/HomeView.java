package bca.sendit.filetransfer.views;

import static fi.iki.elonen.NanoHTTPD.Response.Status;

import android.content.Context;

import java.io.InputStream;

import bca.sendit.filetransfer.paths.ResponseView;
import bca.sendit.filetransfer.server.AssetsHandler;

import fi.iki.elonen.NanoHTTPD;

/**
 * This page is responsible for serving home page.
 */
public class HomeView extends ResponseView {
    @Override
    public NanoHTTPD.Response getResponse(Context context, NanoHTTPD.IHTTPSession session) {
        InputStream inputStream = AssetsHandler.getInputStream(context, "index.html");
        return NanoHTTPD.newChunkedResponse(Status.OK, "text/html", inputStream);
    }
}
