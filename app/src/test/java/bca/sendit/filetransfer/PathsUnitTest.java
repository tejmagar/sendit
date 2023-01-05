package bca.sendit.filetransfer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.app.Instrumentation;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.server.AssetsHandler;
import bca.sendit.filetransfer.server.paths.Path;
import bca.sendit.filetransfer.server.paths.PathMatcher;
import bca.sendit.filetransfer.server.Utils;
import bca.sendit.filetransfer.server.views.PhotosApiView;

public class PathsUnitTest {

    List<Path> pathList = new ArrayList<>();
    Path listPhotosPath;
    PathMatcher pathMatcher;

    @Before
    public void setUp() {
        listPhotosPath = new Path("/api/list-photos/", new PhotosApiView());
        pathList.add(listPhotosPath);
        pathMatcher = new PathMatcher(pathList);
    }

    @Test
    public void path_isMatched() {
        Path matchedPath = pathMatcher.getMatchedPath("/api/list-photos/");
        assertEquals(listPhotosPath, matchedPath);
    }

    @Test
    public void path_isNotMatched() {
        Path matchedPath = pathMatcher.getMatchedPath("/api/version/");
        assertNotEquals(listPhotosPath, matchedPath);
    }

    @Test
    public void path_getMimeType() {
        String mimeType = Utils.getMimeType("scripts.js");
        assertEquals("application-text/js", mimeType);
    }

}
