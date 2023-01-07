package bca.sendit.filetransfer.server.paths;

import android.content.Context;

import bca.sendit.filetransfer.server.request.Request;
import fi.iki.elonen.NanoHTTPD;

public abstract class ResponseView {
    public abstract NanoHTTPD.Response getResponse(Context context, Request request);
}
