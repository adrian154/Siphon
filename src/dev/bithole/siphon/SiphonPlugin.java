package dev.bithole.siphon;

import dev.bithole.siphon.core.Siphon;
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

    }

    @Override
    public void onDisable() {

    }

}
