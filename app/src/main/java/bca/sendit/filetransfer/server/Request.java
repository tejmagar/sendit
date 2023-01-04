package bca.sendit.filetransfer.server;

import fi.iki.elonen.NanoHTTPD;

public class Request {
    private String authToken;
    private boolean isAuthenticated = false;
    private NanoHTTPD.IHTTPSession session;
    private Configuration configuration;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public NanoHTTPD.IHTTPSession getSession() {
        return session;
    }

    public void setSession(NanoHTTPD.IHTTPSession session) {
        this.session = session;
    }
}
