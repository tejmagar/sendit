package bca.sendit.filetransfer.paths;

import android.content.Context;

import fi.iki.elonen.NanoHTTPD;

public abstract class ResponseView {
    public abstract NanoHTTPD.Response getResponse(Context context, NanoHTTPD.IHTTPSession session);
}
