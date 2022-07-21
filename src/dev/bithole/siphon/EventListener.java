package dev.bithole.siphon;

import dev.bithole.siphon.core.Siphon;
import dev.bithole.siphon.core.base.events.*;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class EventListener implements Listener {

    private final Siphon siphon;

    public EventListener(Siphon siphon) {
        this.siphon = siphon;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        siphon.broadcastEvent(new SiphonPlayerJoinEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        siphon.broadcastEvent(new SiphonPlayerQuitEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        siphon.broadcastEvent(new SiphonChatEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getMessage()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        siphon.broadcastEvent(new SiphonPlayerDeathEvent(event.getEntity().getPlayer().getUniqueId(), event.getEntity().getPlayer().getName()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onServerListPing(ServerListPingEvent event) {
        siphon.broadcastEvent(new SiphonPingEvent(event.getAddress()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        System.out.println(event.getAdvancement());
        AdvancementDisplay display = event.getAdvancement().getDisplay();
        if(display == null) {
            System.out.println("display null?");
        }
        siphon.broadcastEvent(new SiphonAdvancementEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), display.getTitle(), display.getDescription()));
        if(!display.shouldAnnounceChat()) {
            System.out.println("not announced in chat: " + display.getTitle());
        }
        if(display.shouldShowToast()) {
            System.out.println("no toast: " + display.getTitle());
        }
    }
    
}
