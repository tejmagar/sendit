package bca.sendit.filetransfer.server;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsReader {

    public static String readFile(Context context, String path) throws IOException {
        if (context == null) {
            throw new IOException();
        }

        InputStream inputStream = context.getAssets().open(path);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStreamReader.close();

        return stringBuilder.toString();
    }
}
