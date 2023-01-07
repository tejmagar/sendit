package bca.sendit.filetransfer.server;

public interface OnServerEvent {
    void onOpenWebSocket(WebSocketServer webSocketServer);
    void onCloseWebSocket(WebSocketServer webSocketServer);
}
