package bca.sendit.filetransfer.server;

import android.content.Context;
import android.content.Intent;

public class Events {

    public final static String ACTION_REQUEST_ALLOW = "action_request_allow";
    public final static String AUTH_TOKEN = "auth_token";
    public final static String REMOTE_IP_ADDRESS = "remote_ip_address";

    /**
     * Send broadcast event to activity to request access resources permission
     * @param request Request
     */
    public static void broadcastAccessRequest(Context context, Request request) {
        Intent intent = new Intent(Events.ACTION_REQUEST_ALLOW);
        intent.putExtra(REMOTE_IP_ADDRESS, request.getSession().getRemoteIpAddress());
        intent.putExtra(AUTH_TOKEN, request.getAuthToken());
        context.sendBroadcast(intent);
    }
}
