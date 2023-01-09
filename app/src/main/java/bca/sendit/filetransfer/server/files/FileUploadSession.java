package bca.sendit.filetransfer.server.files;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.Utils;

public class FileUploadSession {
    private static final List<OnFilesChangeListener> onFilesChangeListeners = new ArrayList<>();

    private static final List<Uri> allowedFiles = new ArrayList<>();

    public static void allowFile(Uri uri) {
        allowedFiles.add(uri);

        for (OnFilesChangeListener listener: onFilesChangeListeners) {
            if (listener != null) {
                listener.onChange();
                listener.onAllowed(uri);
            }
        }
    }

    public static void denyFile(Uri uri) {
        allowedFiles.remove(uri);

        for (OnFilesChangeListener listener: onFilesChangeListeners) {
            if (listener != null) {
                listener.onChange();
                listener.onDeny(uri);
            }
        }
    }

    public static boolean isAllowed(Uri uri) {
        for (Uri allowedFile : allowedFiles) {
            if (allowedFile.toString().equals(uri.toString())) {
                return true;
            }
        }
        return false;
    }

    public static void setCallback(OnFilesChangeListener onFilesChangeListener) {
        onFilesChangeListeners.add(onFilesChangeListener);
    }

    public static void removeCallback(OnFilesChangeListener onFilesChangeListener) {
        onFilesChangeListeners.remove(onFilesChangeListener);
    }

    public static List<Uri> getAllowedFiles() {
        return allowedFiles;
    }

    public static List<Utils.UriFileDetails> getFilesUriDetails(Context context) {
        List<Utils.UriFileDetails> uriFileDetails = new ArrayList<>();

        for (Uri uri: getAllowedFiles()) {
            uriFileDetails.add(Utils.getFileDetails(context, uri));
        }

        return uriFileDetails;
    }
}
