package bca.sendit.filetransfer.server.files;

import android.net.Uri;

public interface OnFilesChangeListener {
    void onAllowed(Uri uri);
    void onDeny(Uri uri);
    void onChange();
}
