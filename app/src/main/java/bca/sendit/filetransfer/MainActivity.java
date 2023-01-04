package bca.sendit.filetransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.Configuration;
import bca.sendit.filetransfer.server.auths.AuthToken;
import bca.sendit.filetransfer.server.auths.Auths;
import bca.sendit.filetransfer.server.paths.Path;
import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.HttpServer;
import bca.sendit.filetransfer.server.views.HomeView;

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
            Configuration configuration = new Configuration();
            configuration.isPrivateMode = true;
            HttpServer httpServer = new HttpServer(this, configuration, pathMatcher, 8080);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Auths.saveToken(this, new AuthToken("fa02f82f558b4e3fa32d9ee19bdc223b", true));
        Db.get(this).authsTokenDao().getTokens().observe(this, new Observer<List<AuthToken>>() {
            @Override
            public void onChanged(List<AuthToken> authTokens) {
                for(AuthToken authToken: authTokens) {
                    Toast.makeText(MainActivity.this, authToken.token, Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(() -> {
//            Auths.saveToken(this, new AuthToken(Auths.generateToken(), true));
        }).start();
    }
}