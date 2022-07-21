package dev.bithole.siphon.core;

import com.google.gson.Gson;
import dev.bithole.siphon.core.handlers.AuthHandler;
import dev.bithole.siphon.core.handlers.ErrorHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.HttpString;

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
    public final Logger logger;

    public Siphon(Logger logger) throws IOException {

        this.logger = logger;
        this.config = new SiphonConfig();
        this.gson = new Gson();

        this.config.addClient(new Client("adrian", "password"));

        // set up path handler, which will control all of our routes
        this.pathHandler = Handlers.path(new DefaultHandler());

        this.server = Undertow.builder()
                .addHttpListener(config.getPort(), "0.0.0.0")
                .setHandler(new ErrorHandler(this, new AuthHandler(this, pathHandler)))
                .build();

        this.server.start();

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

        String permissionNode = "event." + event.name;
        String body = gson.toJson(event);

        System.out.println(body);

        // TODO: deliver to all authorized SSE listeners

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
