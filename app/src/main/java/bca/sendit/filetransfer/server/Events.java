package bca.sendit.filetransfer.server;

import android.content.Context;
import android.content.Intent;

import bca.sendit.filetransfer.server.request.Request;

public class Events {

    public static final String ACTION_REQUEST_ALLOW = "action_request_allow";
    public static final String ACTION_REQUEST_RESULT = "action_request_result";
    public static final String ACTION_ADD_FILE = "action_add_file";
    public static final String ACTION_REMOVE_FILE = "action_remove_file";
    public static final String ACTION_PERMISSION = "action_request_permission";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String REMOTE_IP_ADDRESS = "remote_ip_address";
    public static final String REQUEST_RESULT = "request_result";
    public static final String REQUEST_FILE = "request_file";


    /**
     * Send broadcast event to activity to request access resources permission
     *
     * @param request Request
     */
    public static void broadcastAccessRequest(Context context, Request request) {
        Intent intent = new Intent(Events.ACTION_REQUEST_ALLOW);
        intent.putExtra(REMOTE_IP_ADDRESS, request.getSession().getRemoteIpAddress());
        intent.putExtra(AUTH_TOKEN, request.getAuthToken());
        context.sendBroadcast(intent);
    }
}
