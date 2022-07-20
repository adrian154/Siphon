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
import java.util.logging.Logger;

public class Siphon {

    private Gson gson;
    private ClientsList clients;
    private Undertow server;
    private PathHandler pathHandler;
    public final Logger logger;

    public Siphon(int port, Logger logger) throws IOException {

        this.logger = logger;
        this.clients = new ClientsList();
        this.gson = new Gson();

        // set up path handler, which will control all of our routes
        this.pathHandler = Handlers.path(new DefaultHandler());

        this.server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(new ErrorHandler(this, new AuthHandler(this, pathHandler)))
                .build();

        this.server.start();

    }

    // util method
    public void sendJSON(HttpServerExchange exchange, Object object) {
        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(gson.toJson(object));
    }


    public ClientsList getClients() {
        return clients;
    }

    private static class DefaultHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Not Found");
        }

    }

}
