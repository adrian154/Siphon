package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.APIException;
import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.Siphon;
import dev.bithole.siphon.core.SiphonSecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.Base64;

// Undertow has its own authentication infrastructure, but it's total overkill for our purposes
// so... we've chosen to roll our own
public class AuthHandler implements HttpHandler {

    private final Siphon siphon;
    private final HttpHandler next;

    public AuthHandler(Siphon siphon, HttpHandler next) {
        this.siphon = siphon;
        this.next = next;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String[] parts = exchange.getRequestHeaders().getFirst(new HttpString("Authorization")).split("\\s+");
        if(parts.length != 2) {
            throw new APIException(400, "Invalid authorization header");
        }

        if(!parts[0].equals("Basic")) {
            throw new APIException(400, "Incorrect authorization scheme, must be Basic");
        }

        String[] credentials = new String(Base64.getDecoder().decode(parts[1])).split(":");
        if(credentials.length != 2) {
            throw new APIException(400, "Malformed credentials string");
        }

        Client client = siphon.getClients().getClient(credentials[0]);
        if(client == null || !client.auth(credentials[1])) {
            throw new APIException(401, "Invalid credentials");
        }

        exchange.setSecurityContext(new SiphonSecurityContext(client));
        next.handleRequest(exchange);

    }

}
