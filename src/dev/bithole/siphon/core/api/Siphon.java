package dev.bithole.siphon.core.api;

import dev.bithole.siphon.core.api.SiphonEvent;

public interface Siphon {
    void broadcastEvent(SiphonEvent event);
}
