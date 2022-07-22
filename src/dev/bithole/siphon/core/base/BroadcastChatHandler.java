package dev.bithole.siphon.core.base;

import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.handlers.JsonBodyHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public abstract class BroadcastChatHandler implements HttpHandler {

    protected abstract void broadcastMessage(String string);
    protected final SiphonImpl siphon;

    public BroadcastChatHandler(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        try {
            String message = exchange.getAttachment(JsonBodyHandler.BODY).getAsJsonPrimitive().getAsString();
            if(message == null) {
                throw new APIException(400);
            }
            broadcastMessage(message);
            exchange.endExchange();
        } catch(IllegalStateException ex) {
            throw new APIException(400);
        }

    }

}
