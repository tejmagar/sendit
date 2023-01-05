package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.Request;
import bca.sendit.filetransfer.server.RequestValidator;
import bca.sendit.filetransfer.server.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;


public class VideosApiView extends ResponseView {
    private List<String> listFiles(Context context) {
        List<String> files = new ArrayList<>();

        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                String filePath = cursor.getString(columnIndex);
                files.add(filePath);
            }
            cursor.close();
        }

        return files;
    }

    @Override
    public NanoHTTPD.Response getResponse(Context context, Request request) {
        RequestValidator requestValidator = new RequestValidator(context, request);
        requestValidator.setShouldAuthenticated(true);
        requestValidator.setShouldPrivateMode(false);
        requestValidator.setShouldReadPermission(true);

        if (requestValidator.isValid()) {
            JSONArray jsonArray = new JSONArray();

            for (String file : listFiles(context)) {
                jsonArray.put(file);
            }
            return NanoHTTPD.newFixedLengthResponse(jsonArray.toString());
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.FORBIDDEN,
                "text/plain", requestValidator.getErrorMessage());
    }
}
