package dev.bithole.siphon.core.base.events;

import java.util.UUID;

public class SiphonAdvancementEvent extends PlayerEvent {

    private final String title;
    private final String description;

    public SiphonAdvancementEvent(UUID uuid, String playerName, String title, String description) {
        super("advancement", uuid, playerName);
        this.title = title;
        this.description = description;
    }

}
