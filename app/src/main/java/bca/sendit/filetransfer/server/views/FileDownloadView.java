package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import bca.sendit.filetransfer.server.ResponseTemplate;
import bca.sendit.filetransfer.server.Utils;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.request.Request;
import bca.sendit.filetransfer.server.paths.ResponseView;
import fi.iki.elonen.NanoHTTPD;

public class FileDownloadView extends ResponseView {

    public static final String TAG = "FileDownloadView";

    private String getQueryPath(Map<String, List<String>> parameters) {
        List<String> filePathQuery = parameters.get("file_path");

        if (filePathQuery != null && filePathQuery.size() > 0) {
            return filePathQuery.get(0);
        }

        return null;
    }

    @Override
    public NanoHTTPD.Response getResponse(Context context, Request request) {
        if (request.isAuthenticated()) {
            String filePath = getQueryPath(request.getSession().getParameters());
            Log.d(TAG, "Trying to access resource " + filePath);

            if (filePath != null && (!request.getConfiguration().isPrivateMode
                    || FileUploadSession.isAllowed(filePath))) {
                File file = new File(filePath);

                // If file exist available it to client
                if (file.exists()) {
                    try {
                        InputStream inputStream = new FileInputStream(file);
                        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,
                                Utils.getMimeType(filePath), inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    return ResponseTemplate.fileNotFound(context);
                }
            }
        }

        return ResponseTemplate.forbidden(context, "Access denied");
    }
}
