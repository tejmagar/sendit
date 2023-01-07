package bca.sendit.filetransfer.server;

import android.webkit.MimeTypeMap;

import fi.iki.elonen.NanoHTTPD;


public class Utils {
    /**
     * Returns MimeType for a given url
     * @param url Url or Path
     * @return mimeType
     */
    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
