package dev.bithole.siphon.core.handlers;

import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.SiphonImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HttpString;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

// Undertow has its own authentication infrastructure, but it's total overkill for our purposes
// so... we've chosen to roll our own
public class AuthHandler implements HttpHandler {

    public static final AttachmentKey<Client> CLIENT = AttachmentKey.create(Client.class);
    private static final long AUTH_FAIL_TIMEOUT = 1000;

    private final SiphonImpl siphon;
    private final HttpHandler next;
    private final Map<InetSocketAddress, Long> lastFailedAuth;

    public AuthHandler(SiphonImpl siphon, HttpHandler next) {
        this.siphon = siphon;
        this.next = next;
        this.lastFailedAuth = new HashMap<>();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        // ratelimit clients that send a lot of invalid auth requests
        Long lastAuthFailTime = lastFailedAuth.get(exchange.getSourceAddress());
        if(lastAuthFailTime != null && System.currentTimeMillis() - lastAuthFailTime < AUTH_FAIL_TIMEOUT) {
            throw new APIException(429, "Too many failed authentications");
        }

        String authHeader = exchange.getRequestHeaders().getFirst(new HttpString("Authorization"));
        if(authHeader == null) {
            throw new APIException(401, "You must authenticate to access this API");
        }

        String[] parts = authHeader.split("\\s+");
        if(parts.length != 2) {
            throw new APIException(401, "Invalid authorization header");
        }

        if(!parts[0].equals("Basic")) {
            throw new APIException(401, "Incorrect authorization scheme, must be Basic");
        }

        String[] credentials = new String(Base64.getDecoder().decode(parts[1])).split(":");
        if(credentials.length != 2) {
            throw new APIException(401, "Malformed credentials string");
        }

        Client client = siphon.getConfig().getClient(credentials[0]);
        if(client == null || !client.auth(credentials[1])) {
            lastFailedAuth.put(exchange.getSourceAddress(), System.currentTimeMillis());
            throw new APIException(401, "Invalid credentials");
        }

        exchange.putAttachment(CLIENT, client);
        next.handleRequest(exchange);

    }

}
