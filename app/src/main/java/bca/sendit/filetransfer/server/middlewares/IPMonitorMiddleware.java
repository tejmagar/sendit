package bca.sendit.filetransfer.server.middlewares;

import org.tinyweb.middlewares.Middleware;

import bca.sendit.filetransfer.server.UserSession;
import fi.iki.elonen.NanoHTTPD;

public class IPMonitorMiddleware extends Middleware {
    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession request) {
        UserSession.addDeviceInfo(request.getRemoteIpAddress(), request.getRemoteHostName());
        return null;
    }
}
