package bca.sendit.filetransfer.server;

public class SingletonHttpServer {
    private static SingletonHttpServer singletonHttpServer;
    private final HttpServer httpServer;

    private SingletonHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public static void init(HttpServer httpServer) {
        if (singletonHttpServer == null) {
            singletonHttpServer = new SingletonHttpServer(httpServer);
        }
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public static SingletonHttpServer getInstance() {
        return singletonHttpServer;
    }

}
