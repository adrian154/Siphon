package dev.bithole.siphon.core.base;

import dev.bithole.siphon.core.SiphonImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.UUID;

public abstract class GetPlayersHandler implements HttpHandler {

    protected abstract List<Player> getPlayerList();
    private final SiphonImpl siphon;

    public GetPlayersHandler(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        siphon.sendJSON(exchange, getPlayerList());
    }

    public record Player(UUID uuid, String name) {

    }

}
