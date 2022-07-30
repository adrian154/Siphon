package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class PlayerQuitEvent extends PlayerEvent {

    public PlayerQuitEvent(UUID uuid, String playerName) {
        super("quit", uuid, playerName);
    }

}
