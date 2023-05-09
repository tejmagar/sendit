package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.tinyweb.views.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import bca.sendit.filetransfer.files.FileDetails;
import fi.iki.elonen.NanoHTTPD;

/**
 * This class is for allowing client to download file based on Uri path
 */

public class FileDownloadView extends View {
    private static final String TAG = "FileDownloadView";

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession request) {
        Context context = (Context) getConfigurations().get("context");
        List<String> uris = request.getParameters().get("uri");
        if (uris == null || uris.size() == 0) {
            return error404();
        }

        String uriText = uris.get(0);
        Log.d(TAG, "uri=" + uriText);

        Uri fileUri = Uri.parse(uriText);
        return serveUriFile(context, fileUri);
    }

    private NanoHTTPD.Response error404() {
        return NanoHTTPD.newFixedLengthResponse("404 File Not Found");
    }

    private NanoHTTPD.Response serveUriFile(Context context, Uri uri) {
        try {
            FileDetails fileDetails = new FileDetails(context, uri);
            String fileName = fileDetails.getFilename();
            String mimeType = FileDetails.getMimeType(fileName);

            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            NanoHTTPD.Response response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,
                    mimeType, inputStream);

            if (fileName != null) {
                response.addHeader("Content-Disposition", "inline; filename=\""
                        + fileName + "\"");
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return error404();
    }
}
