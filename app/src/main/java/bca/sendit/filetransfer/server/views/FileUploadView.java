package bca.sendit.filetransfer.server.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.tinyweb.views.View;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bca.sendit.filetransfer.files.FileDownloadMonitor;
import fi.iki.elonen.NanoHTTPD;

public class FileUploadView extends View {
    private static final String TAG = "FileUploadView";

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession request) {
        Context context = (Context)getConfigurations().get("context");
        return uploadResponse(context, request);
    }

    public NanoHTTPD.Response uploadResponse(Context context, NanoHTTPD.IHTTPSession request) {
        Map<String, List<String>> params = request.getParameters();
        Map<String, String> files = new HashMap<>();

        if (NanoHTTPD.Method.POST.equals(request.getMethod())) {
            try {
                request.parseBody(files);

                String filePath = files.get("file");
                List<String> fileNames = params.get("file");

                if (fileNames != null && filePath != null) {
                    String fileName = fileNames.get(0);
                    File out = copyFileToDownloads(filePath, fileName);
                    notifyDownload(context, out.getAbsolutePath());
                    return NanoHTTPD.newFixedLengthResponse("File uploaded to " +
                            out.getAbsolutePath() + " . Go to <a href=\"/\">Home</a>");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return NanoHTTPD.newFixedLengthResponse("Oops, something went wrong");
    }

    private File getDownloadsDir() {
        return new File(Environment.getExternalStorageDirectory() + "/" +
                Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * Generate unique file name for saving. Android allows to write only files in Downloads folder
     * without WRITE_EXTERNAL_STORAGE permission. You can't overwrite files which already exists.
     * @param preferredName your preferred name
     * @return preferredName
     */


    private String generateFilename(String preferredName) {
        File file = new File(getDownloadsDir(), preferredName);
        if (file.exists()) {
            int extensionIndex = preferredName.lastIndexOf(".");

            if (extensionIndex != -1) {
                // File has extension. For eg: example.png
                String filenameWithOutExtension = preferredName.substring(0, extensionIndex);
                String extension = preferredName.substring(extensionIndex);
                return filenameWithOutExtension + "-" + System.currentTimeMillis() + extension;
            } else {
                // This file don't have extension
                return preferredName + "-" + System.currentTimeMillis();
            }
        }

        return preferredName;
    }

    private File copyFileToDownloads(String tmpFilePath, String filename) throws IOException {
        File in = new File(tmpFilePath);
        String outFilename = generateFilename(filename);
        File out = new File(getDownloadsDir(), outFilename);
        Log.i(TAG, "Moving " + in.getAbsolutePath() + " ==> " + out.getAbsolutePath());
        FileUtils.copyFile(in, out);
        return out;
    }


    private void notifySystem(Context context, String path) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + path));
        context.sendBroadcast(scanIntent);
    }

    private void notifyDownload(Context context, String path) {
        notifySystem(context, path);
        FileDownloadMonitor.notifyFileDownloaded(context, path);
    }
}
