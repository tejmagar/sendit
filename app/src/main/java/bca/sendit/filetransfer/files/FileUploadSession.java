package bca.sendit.filetransfer.files;

import java.util.ArrayList;
import java.util.List;

public class FileUploadSession {
    private static FileUploadSession instance;
    private final List<SharedFileItem> sharedFileItems = new ArrayList<>();
    private final List<OnSharedFileListener> filesChangeListeners = new ArrayList<>();

    private FileUploadSession() {
    }

    private void addFileToSession(SharedFileItem sharedFileItem) {
        sharedFileItems.add(sharedFileItem);
        for (OnSharedFileListener onFilesChangeListener : instance.filesChangeListeners) {
            onFilesChangeListener.onChanged(sharedFileItem);
            onFilesChangeListener.onFileAdded(sharedFileItem);
        }
    }

    private void removeFileFromSession(SharedFileItem sharedFileItem) {
        sharedFileItems.remove(sharedFileItem);
        for (OnSharedFileListener onFilesChangeListener : instance.filesChangeListeners) {
            onFilesChangeListener.onChanged(sharedFileItem);
            onFilesChangeListener.onFileRemoved(sharedFileItem);
        }
    }

    private void addListener(OnSharedFileListener onFilesChangeListener) {
        filesChangeListeners.add(onFilesChangeListener);
    }

    private void removeListener(OnSharedFileListener onFilesChangeListener) {
        filesChangeListeners.remove(onFilesChangeListener);
    }

    private List<SharedFileItem> getSharedFileItems() {
        return sharedFileItems;
    }

    public static void init() {
        if (instance == null) {
            instance = new FileUploadSession();
        }
    }

    public static void add(SharedFileItem sharedFileItem) {
        instance.addFileToSession(sharedFileItem);
    }

    public static void remove(SharedFileItem sharedFileItem) {
        instance.removeFileFromSession(sharedFileItem);
    }

    public static List<SharedFileItem> getFiles() {
        return instance.getSharedFileItems();
    }

    public static void attachListener(OnSharedFileListener onFilesChangeListener) {
        instance.addListener(onFilesChangeListener);
    }

    public static void deAttachListener(OnSharedFileListener onSharedFileListener) {
        instance.removeListener(onSharedFileListener);
    }
}
