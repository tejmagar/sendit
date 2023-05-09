package bca.sendit.filetransfer.server.views;

import org.json.JSONException;
import org.json.JSONObject;
import org.tinyweb.views.WsView;
import org.tinyweb.websocket.TinyWebSocket;

import java.io.IOException;

import bca.sendit.filetransfer.files.FileUploadSession;
import bca.sendit.filetransfer.files.OnSharedFileListener;
import bca.sendit.filetransfer.files.SharedFileItem;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class RefreshWsView extends WsView {
    @Override
    public NanoWSD.WebSocket webSocket(NanoHTTPD.IHTTPSession request) {
        return new RefreshWebsocket(request);
    }

    static class RefreshWebsocket extends TinyWebSocket {
        private final OnSharedFileListener onSharedFileListener;

        public RefreshWebsocket(NanoHTTPD.IHTTPSession handshakeRequest) {
            super(handshakeRequest);

            onSharedFileListener = new OnSharedFileListener() {
                @Override
                public void onChanged(SharedFileItem sharedFileItem) {
                    sendRefreshMessage();
                }

                @Override
                public void onFileAdded(SharedFileItem sharedFileItem) {

                }

                @Override
                public void onFileRemoved(SharedFileItem sharedFileItem) {

                }
            };
        }

        private JSONObject createRefreshMessage() throws JSONException {
            JSONObject msg = new JSONObject();
            msg.put("refresh", true);
            return msg;
        }

        private void sendRefreshMessage() {
            new Thread(() -> {
                try {
                    send(createRefreshMessage().toString());
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        @Override
        protected void onOpen() {
            super.onOpen();
            FileUploadSession.attachListener(onSharedFileListener);
        }

        @Override
        protected void onMessage(NanoWSD.WebSocketFrame messageFrame) {
            super.onMessage(messageFrame);
        }

        @Override
        protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            super.onClose(code, reason, initiatedByRemote);
            FileUploadSession.deAttachListener(onSharedFileListener);
        }
    }
}
