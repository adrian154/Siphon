package dev.bithole.siphon.core;

import com.google.gson.Gson;
import dev.bithole.siphon.core.handlers.AuthHandler;
import dev.bithole.siphon.core.handlers.ErrorHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.util.HttpString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class Siphon {

    private Gson gson;
    private SiphonConfig config;
    private Undertow server;
    private PathHandler pathHandler;
    private ServerSentEventHandler sseHandler;
    public final Logger logger;

    public Siphon(Logger logger) throws IOException {

        this.logger = logger;
        this.config = new SiphonConfig();
        this.gson = new Gson();

        this.setupAppender();
        this.addTestClient();

        // set up path handler, which will control all of our routes
        this.pathHandler = Handlers.path(new DefaultHandler());
        this.sseHandler = Handlers.serverSentEvents();
        this.pathHandler.addPrefixPath("/events", sseHandler);

        this.server = Undertow.builder()
                .addHttpListener(config.getPort(), "0.0.0.0")
                .setHandler(new ErrorHandler(this, new AuthHandler(this, pathHandler)))
                .build();

        this.server.start();

    }

    private void addTestClient() throws IOException {
        if(this.config.getClient("test") == null) {
            Client client = new Client("test", "password");
            client.addPermission("*");
            config.addClient(client);
            config.save();
        }
    }

    // I f***ing hate Log4j. It is a monstrosity.
    private void setupAppender() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.getRootLogger().addAppender(new CustomAppender(this));
    }

    // util method
    public void sendJSON(HttpServerExchange exchange, Object object) {
        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(gson.toJson(object));
    }

    public SiphonConfig getConfig() {
        return config;
    }

    public void broadcastEvent(SiphonEvent event) {

        String permissionNode = "event." + event.event;
        String body = gson.toJson(event);

        // deliver to all authorized SSE listeners
        for(ServerSentEventConnection connection: sseHandler.getConnections()) {
            Client client = connection.getAttachment(AuthHandler.CLIENT);
            if(client != null && client.testPermission(permissionNode)) {
                connection.send(body);
            }
        }

        // trigger webhooks
        for(Client client: config.getClients()) {
            URI url = client.getWebhookURL();
            if(url != null && client.testPermission(permissionNode)) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(url)
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "Siphon")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.discarding());
            }
        }

    }

    private static class DefaultHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Not Found");
        }

    }

}
