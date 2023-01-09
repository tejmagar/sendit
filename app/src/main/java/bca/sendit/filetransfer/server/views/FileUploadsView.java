package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import bca.sendit.filetransfer.server.ResponseTemplate;
import bca.sendit.filetransfer.server.Utils;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.paths.ResponseView;
import bca.sendit.filetransfer.server.request.Request;
import fi.iki.elonen.NanoHTTPD;

public class FileUploadsView extends ResponseView {

    @Override
    public NanoHTTPD.Response getResponse(Context context, Request request) {
        if (!request.isAuthenticated()) {
            return ResponseTemplate.forbidden("Not authenticated");
        }

        if (request.getSession().getMethod() == NanoHTTPD.Method.GET) {
            try {
                JSONArray jsonArray = new JSONArray();

                for (Uri uri : FileUploadSession.getAllowedFiles()) {
                    Utils.UriFileDetails uriFileDetails = Utils.getFileDetails(context, uri);

                    if (uriFileDetails != null) {
                        JSONObject data = new JSONObject();
                        // Encode it to prevent decoding issue
                        data.put("uri", uri.toString());
                        data.put("filename", uriFileDetails.getFilename());
                        data.put("size", uriFileDetails.getSize());
                        jsonArray.put(data);
                    }
                }

                return NanoHTTPD.newFixedLengthResponse(jsonArray.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return NanoHTTPD.newFixedLengthResponse("Others");
    }
}
