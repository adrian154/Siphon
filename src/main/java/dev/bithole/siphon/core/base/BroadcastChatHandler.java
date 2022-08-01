package dev.bithole.siphon.core.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.handlers.JsonBodyHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public abstract class BroadcastChatHandler implements HttpHandler {

    protected abstract void broadcastMessage(JsonElement element);
    protected final SiphonImpl siphon;

    public BroadcastChatHandler(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        JsonElement component = exchange.getAttachment(JsonBodyHandler.BODY);
        if(component == null) {
            throw new APIException(400);
        }
        broadcastMessage(component);
        exchange.endExchange();

    }

}
