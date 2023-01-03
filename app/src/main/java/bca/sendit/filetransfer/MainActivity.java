package bca.sendit.filetransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.paths.Path;
import bca.sendit.filetransfer.paths.PathMatcher;
import bca.sendit.filetransfer.server.HttpServer;
import bca.sendit.filetransfer.views.HomeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Path> paths = new ArrayList<>();
        paths.add(new Path("/", new HomeView()));
        paths.add(new Path("/test/", new HomeView()));

        PathMatcher pathMatcher = new PathMatcher(paths);
        try {
            HttpServer httpServer = new HttpServer(this, 8080, pathMatcher);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}