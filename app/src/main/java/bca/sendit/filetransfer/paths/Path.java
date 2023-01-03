package bca.sendit.filetransfer.paths;

public class Path {
    private final String path;
    private final ResponseView responseView;
    private boolean matchExact = true;

    public Path(String path, ResponseView responseView) {
        this.path = path;
        this.responseView = responseView;
    }

    public Path(String path, ResponseView responseView, boolean matchExact) {
        this.path = path;
        this.responseView = responseView;
        this.matchExact = matchExact;
    }

    public String getPath() {
        return path;
    }

    public ResponseView getResponseView() {
        return responseView;
    }

    public boolean isMatchExact() {
        return matchExact;
    }
}
