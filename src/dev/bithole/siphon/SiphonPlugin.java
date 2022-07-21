package dev.bithole.siphon;

import dev.bithole.siphon.core.Siphon;
import dev.bithole.siphon.core.SiphonEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SiphonPlugin extends JavaPlugin {

    private Siphon siphon;
    private EventListener eventListener;

    public Siphon getSiphon() {
        return siphon;
    }

    @Override
    public void onEnable() {

        try {
            this.siphon = new Siphon(this.getLogger());
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
