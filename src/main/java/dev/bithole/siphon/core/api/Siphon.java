package dev.bithole.siphon.core.api;

import dev.bithole.siphon.core.api.SiphonEvent;
import io.undertow.server.HttpHandler;

public interface Siphon {
    void broadcastEvent(SiphonEvent event);
    void addRoute(String method, String path, HttpHandler handler, String permission);
}
