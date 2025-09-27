package ca.ulaval.glo4003;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://0.0.0.0:8080/api/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("ca.ulaval.glo4003.trotti.user.controller",
                        "ca.ulaval.glo4003.trotti.pass.controller");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        final HttpServer server = startServer();
        System.out.printf("Server started on %s%n", BASE_URI);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
    }
}
