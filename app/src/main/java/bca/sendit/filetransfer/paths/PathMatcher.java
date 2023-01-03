package bca.sendit.filetransfer.paths;

import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class PathMatcher {
    private final List<Path> paths;

    /**
     * Initialize with paths
     *
     * @param paths List of path for generating response
     */
    public PathMatcher(List<Path> paths) {
        this.paths = paths;
    }

    /**
     * Returns matching <code>Path</code>. It will return null if current path not matched.
     *
     * @param currentPath relative url
     * @return path - matched path
     */
    public Path getMatchedPath(String currentPath) {
        for (Path path : paths) {

            // Check for exact match
            if (path.getPath().equals(currentPath) && path.isMatchExact()) {
                return path;
            }
        }

        return null;
    }

    /**
     * Returns matched response view with corresponding to paths set on constructor
     * <code>PathMatcher</code>. Will return null if not matched.
     *
     * @param session NanoHTTPD session
     * @return responseView - matched ResponseView
     */
    public ResponseView getMatchedView(NanoHTTPD.IHTTPSession session) {
        String currentPath = session.getUri();
        Path matchedPath = getMatchedPath(currentPath);

        if (matchedPath != null) {
            return matchedPath.getResponseView();
        }

        return null;
    }
}
