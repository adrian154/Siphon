package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.SiphonConfig;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.apache.http.client.methods.HttpPatch;

public class CORSHandler implements HttpHandler {

    private static final HttpString METHOD_OPTIONS = new HttpString("OPTIONS");

    private final SiphonConfig config;
    private final HttpHandler next;

    public CORSHandler(SiphonConfig config, HttpHandler next) {
        this.config = config;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String origin = exchange.getRequestHeaders().getFirst(new HttpString("Origin"));
        if(config.originIsAllowed(origin)) {
            exchange.getResponseHeaders()
                    .put(new HttpString("Access-Control-Allow-Origin"), origin)
                    .put(new HttpString("Access-Control-Allow-Credentials"), "true");
        }

        // handle CORS preflight
        if(exchange.getRequestMethod().equals(METHOD_OPTIONS)) {
            exchange.setStatusCode(204);
            exchange.getResponseHeaders()
                    .put(new HttpString("Access-Control-Allow-Methods"), "GET, HEAD, POST, PUT, DELETE")
                    .put(new HttpString("Access-Control-Max-Age"), "86400")
                    .put(new HttpString("Access-Control-Allow-Headers"), "Content-Type");
            exchange.endExchange();
        } else {
            next.handleRequest(exchange);
        }

    }

}
