package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class SiphonChatEvent extends PlayerEvent {

    private final String message;

    public SiphonChatEvent(UUID uuid, String playerName, String message) {
        super("chat", uuid, playerName);
        this.message = message;
    }

}
