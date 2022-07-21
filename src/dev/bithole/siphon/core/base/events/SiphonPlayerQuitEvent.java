package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class SiphonPlayerQuitEvent extends PlayerEvent {

    public SiphonPlayerQuitEvent(UUID uuid, String playerName) {
        super("quit", uuid, playerName);
    }

}
