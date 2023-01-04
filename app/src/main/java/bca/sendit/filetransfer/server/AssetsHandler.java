package bca.sendit.filetransfer.server;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetsHandler {

    /**
     * Returns input stream of file if found in project assets directory.
     * It will return null if exception is occurred.
     *
     * @param context context
     * @param path    filepath
     * @return inputStream
     */
    public static InputStream getInputStream(Context context, String path) {

        try {
            return context.getAssets().open(path);
        } catch (IOException ignore) {
        }

        return null;
    }
}
