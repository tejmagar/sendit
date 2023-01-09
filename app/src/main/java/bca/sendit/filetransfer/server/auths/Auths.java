package bca.sendit.filetransfer.server.auths;

import android.content.Context;

import java.util.UUID;

import bca.sendit.filetransfer.DbManager;
import bca.sendit.filetransfer.server.WebSocketServer;

public class Auths {
    /**
     * Generates new token with random UUID
     *
     * @return token
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * Saves auth token to the database.
     *
     * @param context   context
     * @param authToken AuthToken
     */
    public static void saveToken(Context context, AuthToken authToken) {
        new Thread(() -> {
            if (getToken(context, authToken.token) != null) {
                return;
            }

            DbManager.get(context).authsTokenDao().addToken(authToken);
        }).start();
    }

    /**
     * Returns token if exist
     *
     * @param context Context
     * @param token   Token
     * @return AuthToken
     */
    public static AuthToken getToken(Context context, String token) {
        return DbManager.get(context).authsTokenDao().getToken(token, true);
    }

    /**
     * Delete token from the database
     *
     * @param context   Context
     * @param authToken AuthToken
     */
    public static void deleteToken(Context context, AuthToken authToken) {
        new Thread(() -> DbManager.get(context).authsTokenDao().deleteToken(authToken)).start();
    }


    public static boolean isAuthenticated(Context context, String token) {
        if (token == null) {
            return false;
        }

        AuthToken authToken = getToken(context, token);
        return authToken != null;
    }

    /**
     * Hardcoded
     * @param uri String
     * @return token
     */
    public static String getTokenFromUri(String uri) {
        String token = uri.replaceFirst("/websocket/", "");
        token = token.replace("/", "");
        return token;
    }

    public static boolean isAuthenticated(Context context, WebSocketServer webSocketServer) {
        String uri = webSocketServer.getHandshakeRequest().getUri();
        String authToken = getTokenFromUri(uri);

        return isAuthenticated(context, authToken);
    }

}
