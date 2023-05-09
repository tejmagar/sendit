package bca.sendit.filetransfer.server;

import org.tinyweb.TinyWebWS;
import org.tinyweb.middlewares.Middlewares;
import org.tinyweb.urls.Path;
import org.tinyweb.urls.Routes;

import bca.sendit.filetransfer.server.middlewares.IPMonitorMiddleware;
import bca.sendit.filetransfer.server.views.FileDownloadView;
import bca.sendit.filetransfer.server.views.FileUploadView;
import bca.sendit.filetransfer.server.views.HomeView;
import bca.sendit.filetransfer.server.views.RefreshWsView;

public class Server {

    private static TinyWebWS instance;
    private static int port = 8000;

    public static Routes createRoutes() {
        Routes routes = new Routes();
        routes.addRoute(new Path("/", new HomeView()));
        routes.addRoute(new Path("/download/", new FileDownloadView()));
        routes.addRoute(new Path("/upload/", new FileUploadView()));
        return routes;
    }

    public static Routes createWsRoutes() {
        Routes routes = new Routes();
        routes.addRoute(new Path("/ws/", new RefreshWsView()));
        return routes;
    }

    public static Middlewares createMiddlewares() {
        Middlewares middlewares = new Middlewares();
        middlewares.add(new IPMonitorMiddleware());
        return middlewares;
    }

    private static void load(TinyWebWS tinyWebWS) {
        tinyWebWS.setRoutes(createRoutes());
        tinyWebWS.setWsRoutes(createWsRoutes());
        tinyWebWS.setMiddlewares(createMiddlewares());
    }

    public static void setPort(int port) {
        Server.port = port;
    }

    public static TinyWebWS getInstance() {
        if (instance == null) {
            instance = new TinyWebWS(port);
            load(instance);
        }
        return instance;
    }
}
