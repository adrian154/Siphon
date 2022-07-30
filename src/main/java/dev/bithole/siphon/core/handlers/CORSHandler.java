package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.SiphonConfig;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class CORSHandler implements HttpHandler {

    private final SiphonConfig config;
    private final HttpHandler next;

    public CORSHandler(SiphonConfig config, HttpHandler next) {
        this.config = config;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if(config.allowCrossOrigin()) {
            httpServerExchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        }
        next.handleRequest(httpServerExchange);
    }

}
