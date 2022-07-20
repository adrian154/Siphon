package dev.bithole.siphon;

import dev.bithole.siphon.core.Siphon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SiphonPlugin extends JavaPlugin {

    private Siphon siphon;

    public Siphon getSiphon() {
        return siphon;
    }

    @Override
    public void onEnable() {
        try {
            this.siphon = new Siphon(8080, this.getLogger());
        } catch(IOException ex) {
            throw new IllegalArgumentException("Failed to instantiate server", ex);
        }
    }

    @Override
    public void onDisable() {

    }

}
