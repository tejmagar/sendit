package bca.sendit.filetransfer.files;

import android.net.Uri;

public class SharedFileItem {
    private final String name;
    private final long size;
    private final Uri uri;

    public SharedFileItem(String name, long size, Uri uri) {
        this.name = name;
        this.size = size;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Uri getUri() {
        return uri;
    }

    public static SharedFileItem create(String name, long size, Uri uri) {
        return new SharedFileItem(name, size, uri);
    }
}
