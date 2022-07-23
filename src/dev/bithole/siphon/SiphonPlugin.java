package dev.bithole.siphon;

import dev.bithole.siphon.base.BroadcastChatHandlerImpl;
import dev.bithole.siphon.base.GetPlayersHandlerImpl;
import dev.bithole.siphon.base.RunCommandHandlerImpl;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.Siphon;
import dev.bithole.siphon.core.api.SiphonEvent;
import org.bukkit.Server;
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

        // register base API
        Server server = this.getServer();
        siphon.addRoute("GET", "/players", new GetPlayersHandlerImpl(siphon, server), "players.get");
        siphon.addRoute("POST", "/command", new RunCommandHandlerImpl(this, siphon, server), "command.run");
        siphon.addRoute("POST", "/chat", new BroadcastChatHandlerImpl(this, siphon, server), "chat.broadcast");

        // register commands
        this.getCommand("addclient").setExecutor(new AddClientCommand(siphon));
        this.getCommand("removeclient").setExecutor(new RemoveClientCommand(siphon));

    }

    @Override
    public void onDisable() {
        siphon.broadcastEvent(new SiphonEvent("disable"));
    }

}
