package bca.sendit.filetransfer.server.views;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tinyweb.views.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.files.FileUploadSession;
import bca.sendit.filetransfer.files.SharedFileItem;
import bca.sendit.filetransfer.server.AssetsReader;
import fi.iki.elonen.NanoHTTPD;

public class HomeView extends View {

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession request) {
        return renderHtml();
    }

    private JSONArray createJsonForSharedItems(List<SharedFileItem> sharedFileItems) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (SharedFileItem sharedFileItem : sharedFileItems) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("filename", sharedFileItem.getName());
            jsonObject.put("size", sharedFileItem.getSize());
            jsonObject.put("uri", sharedFileItem.getUri());
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    private NanoHTTPD.Response renderHtml() {
        List<SharedFileItem> files = FileUploadSession.getFiles();
        Context context = (Context) getConfigurations().get("context");

        try {
            String contents = AssetsReader.readFile(context, "index.html");
            contents = contents.replace("{{ app_name }}", context.getString(R.string.app_name));
            contents = contents.replace("{{ files }}", createJsonForSharedItems(files).toString());
            return NanoHTTPD.newFixedLengthResponse(contents);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return NanoHTTPD.newFixedLengthResponse("Oops, something went wrong.");
    }
}
