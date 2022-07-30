package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class PlayerDeathEvent extends PlayerEvent {

    private final String message;

    public PlayerDeathEvent(UUID uuid, String playerName, String message) {
        super("player-death", uuid, playerName);
        this.message = message;
    }

}
