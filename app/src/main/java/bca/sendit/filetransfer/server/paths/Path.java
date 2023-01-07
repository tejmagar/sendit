package bca.sendit.filetransfer.server.paths;

public class Path {
    private final String path;
    private final ResponseView responseView;
    private boolean matchStart = false;

    public Path(String path, ResponseView responseView) {
        this.path = path;
        this.responseView = responseView;
    }

    public void setMatchStart(boolean match) {
        this.matchStart = match;
    }

    public String getPath() {
        return path;
    }

    public boolean isMatchStart() {
        return matchStart;
    }

    public ResponseView getResponseView() {
        return responseView;
    }

}
