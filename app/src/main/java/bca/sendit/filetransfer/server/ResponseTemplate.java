package bca.sendit.filetransfer.server;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

    public static NanoHTTPD.Response forbidden(String text) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.FORBIDDEN,
                "text/plain", text);
    }

    public static NanoHTTPD.Response serveFile(Context context, String path) {
        try {
            String mimeType = Utils.getMimeType(path);
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            NanoHTTPD.Response response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,
                    mimeType, inputStream);
            response.addHeader("Content-Disposition", "inline; filename=\""
                    + file.getName() + "\"");
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileNotFound(context);
    }

    public static NanoHTTPD.Response serveUriFile(Context context, Uri uri) {
        try {
            Utils.UriFileDetails uriFileDetails = Utils.getFileDetails(context, uri);
            String mimeType = "application/octet-stream";
            String fileName = null;

            if (uriFileDetails != null) {
                fileName = uriFileDetails.getFilename();
                mimeType = Utils.getMimeType(fileName);
            }

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            NanoHTTPD.Response response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType, inputStream);

            if (fileName != null) {
                response.addHeader("Content-Disposition", "inline; filename=\""
                        + fileName + "\"");
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNotFound(context);
    }
}
