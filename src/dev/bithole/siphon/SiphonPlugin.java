package dev.bithole.siphon;

import dev.bithole.siphon.core.api.Siphon;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.SiphonEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SiphonPlugin extends JavaPlugin {

    private SiphonImpl siphon;
    private EventListener eventListener;

    public Siphon getSiphon() {
        return siphon;
    }

    @Override
    public void onEnable() {

        try {
            this.siphon = new SiphonImpl(this.getLogger());
        } catch(IOException ex) {
            throw new IllegalArgumentException("Failed to instantiate server", ex);
        }

        this.eventListener = new EventListener(siphon);
        this.getServer().getPluginManager().registerEvents(eventListener, this);

        // broadcast enable event
        siphon.broadcastEvent(new SiphonEvent("enable"));

    }

    @Override
    public void onDisable() {
        siphon.broadcastEvent(new SiphonEvent("disable"));
    }

}
