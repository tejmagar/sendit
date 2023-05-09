package bca.sendit.filetransfer.files;

public interface OnSharedFileListener {
    void onChanged(SharedFileItem sharedFileItem);
    void onFileAdded(SharedFileItem sharedFileItem);
    void onFileRemoved(SharedFileItem sharedFileItem);
}
