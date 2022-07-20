package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.APIException;
import dev.bithole.siphon.core.Siphon;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.logging.Level;

public class ErrorHandler implements HttpHandler {

    private final Siphon siphon;
    private final HttpHandler next;

    public ErrorHandler(Siphon siphon, HttpHandler next) {
        this.siphon = siphon;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            next.handleRequest(exchange);
        } catch(Exception ex) {
            if(ex instanceof APIException) {
                APIException apiException = (APIException) ex;
                exchange.setStatusCode(apiException.status);
                siphon.sendJSON(exchange, new APIException.ErrorResponse(apiException));
            } else {
                siphon.logger.log(Level.SEVERE, "Error while processing request", ex);
                exchange.setStatusCode(500);
                exchange.getResponseSender().send("Internal server error");
            }
        }
    }

}
