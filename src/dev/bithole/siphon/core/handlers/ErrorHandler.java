package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.SiphonImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.logging.Level;

public class ErrorHandler implements HttpHandler {

    private final SiphonImpl siphon;
    private final HttpHandler next;

    public ErrorHandler(SiphonImpl siphon, HttpHandler next) {
        this.siphon = siphon;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        try {
            next.handleRequest(exchange);
        } catch(Exception ex) {
            if(ex instanceof APIException apiException) {
                exchange.setStatusCode(apiException.status);
                if(apiException.status == 401) {
                    exchange.getResponseHeaders().put(new HttpString("WWW-Authenticate"), "Basic realm=\"Siphon\"");
                }
                siphon.sendJSON(exchange, new APIException.ErrorResponse(apiException));
            } else {
                siphon.logger.log(Level.SEVERE, "Error while processing request", ex);
                exchange.setStatusCode(500);
                siphon.sendJSON(exchange, new APIException.ErrorResponse("Internal server error"));
            }
        }
    }

}
