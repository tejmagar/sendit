package bca.sendit.filetransfer.server.files;

import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final List<String> allowedFiles = new ArrayList<>();

    public static void addFile(String path) {
        allowedFiles.add(path);
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

    public static List<String> getAllowedFiles() {
        return allowedFiles;
    }
}
