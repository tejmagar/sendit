package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import bca.sendit.filetransfer.server.Configuration;
import bca.sendit.filetransfer.server.ResponseTemplate;
import bca.sendit.filetransfer.server.Utils;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.request.Request;
import bca.sendit.filetransfer.server.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;

public class FileDownloadView extends ResponseView {

    public static final String TAG = "FileDownloadView";

    /*
     * Resources can be accessed using file path and with content Uri.
     * Check path for both methods
     */
    private String getQueryPath(Map<String, List<String>> parameters, String query) {
        List<String> pathQuery = parameters.get(query);

        if (pathQuery != null && pathQuery.size() > 0) {
            return pathQuery.get(0);
        }

        return null;
    }

    public boolean hasAccessPermission(Configuration configuration, Uri fileUri) {
        return fileUri != null && (!configuration.isPrivateMode || FileUploadSession.isAllowed(fileUri));
    }

    @Override
    public NanoHTTPD.Response getResponse(Context context, Request request) {
        if (request.isAuthenticated()) {
            Map<String, List<String>> parameters = request.getSession().getParameters();
            String filePath = getQueryPath(parameters, "file_path");

            // Handle file_path query at first
            // Files cannot be be served in private mode
            if (filePath != null && !request.getConfiguration().isPrivateMode) {
                Log.d(TAG, "Trying to access resource " + filePath);
                return ResponseTemplate.serveFile(context, filePath);
            }

            // Handle content uri
            String contentUri = getQueryPath(parameters, "uri");

            if (contentUri != null) {
                Uri fileUri = Uri.parse(contentUri);

                if (fileUri != null && hasAccessPermission(request.getConfiguration(), fileUri)) {
                    return ResponseTemplate.serveUriFile(context, fileUri);
                }
            }
        }

        return ResponseTemplate.forbidden("Access denied");
    }
}
