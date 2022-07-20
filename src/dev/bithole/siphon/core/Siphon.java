package dev.bithole.siphon.core;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

public class Siphon {

    private ClientsList clients;
    private Undertow server;
    private PathHandler pathHandler;

    public Siphon(int port, File clientsFile) throws IOException {

        this.clients = new ClientsList(clientsFile);

        // set up path handler, which will control all of our routes
        this.pathHandler = Handlers.path(new DefaultHandler());

        this.server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(buildHandler())
                .build();

        this.server.start();

    }

    private HttpHandler buildHandler() {

        // Undertow has built-in security infrastructure, but frankly I find it to be ridiculously overengineered
        // it's much easier to roll our own version
        

    }

    private static class DefaultHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Not Found");
        }

    }

}
