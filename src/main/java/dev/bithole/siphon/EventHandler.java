package dev.bithole.siphon;

import com.mojang.realmsclient.client.Ping;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.Siphon;
import dev.bithole.siphon.core.api.SiphonEvent;
import dev.bithole.siphon.core.base.events.ChatEvent;
import dev.bithole.siphon.core.base.events.PlayerDeathEvent;
import dev.bithole.siphon.core.base.events.PlayerJoinEvent;
import dev.bithole.siphon.core.base.events.PlayerQuitEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    private SiphonMod mod;
    private SiphonImpl siphon;

    public EventHandler(SiphonMod mod, SiphonImpl siphon) {
        this.mod = mod;
        this.siphon = siphon;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        siphon.broadcastEvent(new PlayerJoinEvent(event.getPlayer().getUUID(), event.getPlayer().getScoreboardName()));
    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        siphon.broadcastEvent(new PlayerQuitEvent(event.getPlayer().getUUID(), event.getPlayer().getScoreboardName()));
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        siphon.broadcastEvent(new ChatEvent(event.getPlayer().getUUID(), event.getPlayer().getScoreboardName(), event.getMessage()));
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            siphon.broadcastEvent(new PlayerDeathEvent(player.getUUID(), player.getScoreboardName(), event.getSource().getLocalizedDeathMessage(player).getString()));
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        siphon.broadcastEvent(new SiphonEvent("enable"));
        mod.setServer(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        siphon.broadcastEvent(new SiphonEvent("disable"));
    }

}
