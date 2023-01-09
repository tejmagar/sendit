package bca.sendit.filetransfer.server;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;


public class Utils {
    public static class UriFileDetails {
        private final String filename;
        private final Long size;
        private final Uri uri;

        public UriFileDetails(Uri uri, String filename, Long size) {
            this.uri = uri;
            this.filename = filename;
            this.size = size;
        }

        public Uri getUri() {
            return uri;
        }

        public String getFilename() {
            return filename;
        }

        public long getSize() {
            return size;
        }
    }

    /**
     * Returns MimeType for a given url
     *
     * @param url Url or Path
     * @return mimeType
     */
    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * Returns file details from content Uri. If not found, value returned will be null
     *
     * @param context context
     * @param uri     Uri
     * @return filename
     */
    public static UriFileDetails getFileDetails(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            String filename = cursor.getString(nameIndex);
            Long size = cursor.getLong(sizeIndex);
            cursor.close();

            return new UriFileDetails(uri, filename, size);
        }

        return null;
    }
}
