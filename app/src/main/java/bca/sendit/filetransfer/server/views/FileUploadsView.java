package bca.sendit.filetransfer.server.views;

import android.content.Context;

import org.json.JSONArray;

import bca.sendit.filetransfer.server.ResponseTemplate;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.paths.ResponseView;
import bca.sendit.filetransfer.server.request.Request;
import fi.iki.elonen.NanoHTTPD;

public class FileUploadsView extends ResponseView {

    @Override
    public NanoHTTPD.Response getResponse(Context context, Request request) {
        if (!request.isAuthenticated()) {
            return ResponseTemplate.forbidden(context, "Not authenticated");
        }

        if (request.getSession().getMethod() == NanoHTTPD.Method.GET) {
            JSONArray jsonArray = new JSONArray();

            for (String path : FileUploadSession.getAllowedFiles()) {
                jsonArray.put(path);
            }

            return NanoHTTPD.newFixedLengthResponse(jsonArray.toString());
        }

        return NanoHTTPD.newFixedLengthResponse("Others");
    }
}
