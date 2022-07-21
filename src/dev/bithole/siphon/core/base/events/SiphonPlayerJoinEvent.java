package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class SiphonPlayerJoinEvent extends PlayerEvent {

    public SiphonPlayerJoinEvent(UUID uuid, String playerName) {
        super("player-join", uuid, playerName);
    }

}
