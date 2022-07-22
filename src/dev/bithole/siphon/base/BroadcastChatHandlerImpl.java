package dev.bithole.siphon.base;

import dev.bithole.siphon.SiphonPlugin;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.BroadcastChatHandler;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class BroadcastChatHandlerImpl extends BroadcastChatHandler {

    private final SiphonPlugin plugin;
    private final Server server;

    public BroadcastChatHandlerImpl(SiphonPlugin plugin, SiphonImpl siphon, Server server) {
        super(siphon);
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    protected void broadcastMessage(String message) {
        for(Player player: server.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

}
