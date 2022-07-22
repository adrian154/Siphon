package dev.bithole.siphon.core.base;

import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.APIException;
import dev.bithole.siphon.core.handlers.JsonBodyHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public abstract class RunCommandHandler implements HttpHandler {

    protected abstract void runCommand(String command);
    protected final SiphonImpl siphon;

    public RunCommandHandler(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        try {
            String command = exchange.getAttachment(JsonBodyHandler.BODY).getAsJsonPrimitive().getAsString();
            if(command == null) {
                throw new APIException(400);
            }
            runCommand(command);
            exchange.endExchange();
        } catch(IllegalStateException ex) {
            throw new APIException(400);
        }

    }

    public record Response(boolean success, String message) {

    }

}
