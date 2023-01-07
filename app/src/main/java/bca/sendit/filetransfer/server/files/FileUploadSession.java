package bca.sendit.filetransfer.server.files;

import java.util.ArrayList;
import java.util.List;

public class FileUploadSession {
    private static final List<OnFilesChangeListener> onFilesChangeListeners = new ArrayList<>();

    private static final List<String> allowedFiles = new ArrayList<>();

    public static void addFile(String path) {
        allowedFiles.add(path);

        for (OnFilesChangeListener listener: onFilesChangeListeners) {
            if (listener != null) {
                listener.onAdded(path);
            }
        }
    }

    public static void removeFile(String path) {
        allowedFiles.remove(path);
    }

    public static boolean isAllowed(String file) {
        for (String allowedFile : allowedFiles) {
            if (allowedFile.equals(file)) {
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


    public static List<String> getAllowedFiles() {
        return allowedFiles;
    }
}
