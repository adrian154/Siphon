package dev.bithole.siphon.core;

import com.google.gson.Gson;
import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.api.Siphon;
import dev.bithole.siphon.core.api.SiphonEvent;
import dev.bithole.siphon.core.base.events.LogMessageEvent;
import dev.bithole.siphon.core.handlers.AuthHandler;
import dev.bithole.siphon.core.handlers.CORSHandler;
import dev.bithole.siphon.core.handlers.ErrorHandler;
import dev.bithole.siphon.core.handlers.JsonBodyHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.util.HttpString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SiphonImpl implements Siphon {

    private final Gson gson;
    private final SiphonConfig config;
    private final Undertow server;
    private final PathHandler pathHandler;
    private final ServerSentEventHandler sseHandler;
    public final Logger logger;

    public SiphonImpl(Logger logger) throws IOException {

        this.logger = logger;
        this.config = new SiphonConfig();
        this.gson = new Gson();

        this.setupAppender();

        // set up path handler, which will control all of our routes
        this.pathHandler = Handlers.path(new DefaultHandler());
        this.sseHandler = Handlers.serverSentEvents();
        this.pathHandler.addPrefixPath("/events", sseHandler);

        this.server = Undertow.builder()
                .addHttpListener(config.getPort(), "0.0.0.0")
                .setHandler(new ErrorHandler(this,
                        new CORSHandler(config,
                                new AuthHandler(this,
                                        new JsonBodyHandler(
                                                pathHandler)))))
                .build();

        this.server.start();

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

    @Override
    public void addRoute(String method, String path, HttpHandler handler, String permission) {

        HttpString methodStr = new HttpString(method);
        pathHandler.addPrefixPath(path, exchange -> {

            // make sure method is right
            if(!exchange.getRequestMethod().equals(methodStr)) {
                throw new APIException(405, "Method not allowed");
            }

            // check if client is authenticated
            if(!exchange.getAttachment(AuthHandler.CLIENT).testPermission(permission)) {
                throw new APIException(403, "You are not authorized to access that endpoint");
            }

            handler.handleRequest(exchange);

        });

    }

    private static class DefaultHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Not Found");
        }

    }

    @Plugin(name="Siphon", category= Core.CATEGORY_NAME, elementType= Appender.ELEMENT_TYPE)
    public static class CustomAppender extends AbstractAppender {

        private final SiphonImpl siphon;

        public CustomAppender(SiphonImpl siphon) {
            super(
                    "SiphonAppender",
                    null,
                    PatternLayout.newBuilder().withPattern("%msg").build(),
                    false,
                    null
            );
            this.siphon = siphon;
        }

        @Override
        public void append(LogEvent event) {
            siphon.broadcastEvent(new LogMessageEvent(event.toImmutable()));
        }

        @Override
        public boolean isStarted() {
            return true;
        }

    }
}
