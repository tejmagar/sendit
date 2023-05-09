package bca.sendit.filetransfer.files;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.IOException;

public class FileDetails {
    private String filename;
    private long size;

    public FileDetails(Context context, Uri uri) throws IOException {
        resolveUri(context, uri);
    }

    private void resolveUri(Context context, Uri uri) throws IOException {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);

        if (cursor != null) {
            boolean moved = cursor.moveToFirst();

            if (!moved) {
                throw new IOException();
            }

            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            filename = cursor.getString(nameIndex);
            size = cursor.getLong(sizeIndex);
            cursor.close();
        }

    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
