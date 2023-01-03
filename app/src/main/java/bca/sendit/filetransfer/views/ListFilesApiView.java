package bca.sendit.filetransfer.views;

import android.content.Context;

import org.json.JSONObject;

import bca.sendit.filetransfer.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;

public class ListFilesApiView extends ResponseView {

    @Override
    public NanoHTTPD.Response getResponse(Context context, NanoHTTPD.IHTTPSession session) {
        JSONObject jsonObject = new JSONObject();
        return NanoHTTPD.newFixedLengthResponse("");
    }
}
