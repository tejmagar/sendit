package bca.sendit.filetransfer.files;

import android.net.Uri;

import java.util.List;

public interface OnFileChooseListener {
    void onFilesSelected(List<Uri> uris);
}
