package dev.bithole.siphon.core.base.events;

import dev.bithole.siphon.core.SiphonEvent;

import java.util.UUID;

// Parent for messages involving one player
public abstract class PlayerEvent extends SiphonEvent {

    private final UUID uuid;
    private final String playerName;

    public PlayerEvent(String name, UUID uuid, String playerName) {
        super(name);
        this.uuid = uuid;
        this.playerName = playerName;
    }

}
