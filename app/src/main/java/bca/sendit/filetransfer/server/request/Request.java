package bca.sendit.filetransfer.server;

import android.content.Context;

import bca.sendit.filetransfer.server.auths.Auths;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.CookieHandler;

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

    public static String getAuthToken(CookieHandler cookieHandler) {
        return cookieHandler.read("Authorization: Token");
    }

    /**
     * Build request object with necessary information for passing to class extending ResponseView
     *
     * @param context       Context
     * @param session       IHTTPSession
     * @param authToken     authToken
     * @param configuration Server Configuration
     * @return request Request
     */
    public static Request build(Context context, NanoHTTPD.IHTTPSession session,
                                String authToken, Configuration configuration) {
        Request request = new Request();
        if (authToken == null) {
            authToken = Auths.generateToken();
        }
        request.setAuthToken(authToken);
        request.setSession(session);
        request.setAuthenticated(Auths.isAuthenticated(context, authToken));
        request.setConfiguration(configuration);
        return request;
    }

}
