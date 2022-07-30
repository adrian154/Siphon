package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class PlayerJoinEvent extends PlayerEvent {

    public PlayerJoinEvent(UUID uuid, String playerName) {
        super("player-join", uuid, playerName);
    }

}
