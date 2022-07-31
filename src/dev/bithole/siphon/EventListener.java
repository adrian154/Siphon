package dev.bithole.siphon;

import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class EventListener implements Listener {

    private final SiphonImpl siphon;

    public EventListener(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        siphon.broadcastEvent(new PlayerJoinEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        siphon.broadcastEvent(new PlayerQuitEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        siphon.broadcastEvent(new ChatEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getMessage()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        siphon.broadcastEvent(new PlayerDeathEvent(event.getEntity().getPlayer().getUniqueId(), event.getEntity().getPlayer().getName(), event.getDeathMessage()));
    }

}
