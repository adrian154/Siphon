package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class SiphonPlayerDeathEvent extends PlayerEvent {

    public SiphonPlayerDeathEvent(UUID uuid, String playerName) {
        super("player-death", uuid, playerName);
    }

}
