package dev.bithole.siphon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import dev.bithole.siphon.base.BroadcastChatHandlerImpl;
import dev.bithole.siphon.base.GetPlayersHandlerImpl;
import dev.bithole.siphon.base.RunCommandHandlerImpl;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.api.SiphonEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;

@Mod("siphon")
public class SiphonMod {

    private static final Logger LOGGER = LogUtils.getLogger();
    private SiphonImpl siphon;
    private MinecraftServer server;

    public SiphonMod() {

        try {
            this.siphon = new SiphonImpl(LOGGER);
        } catch(IOException ex) {
            throw new RuntimeException("Failed to instantiate server", ex);
        }

        MinecraftForge.EVENT_BUS.register(new EventHandler(siphon));
        siphon.addRoute("GET", "/players", new GetPlayersHandlerImpl(this, siphon), "players.get");
        siphon.addRoute("POST", "/command", new RunCommandHandlerImpl(this, siphon), "command.run");
        siphon.addRoute("POST", "/chat", new BroadcastChatHandlerImpl(this, siphon), "chat.broadcast");

    }

    public MinecraftServer getServer() {
        return server;
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        siphon.broadcastEvent(new SiphonEvent("enable"));
        this.server = server;
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        siphon.broadcastEvent(new SiphonEvent("disable"));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        AddClientCommand.register(dispatcher, siphon);
        RemoveClientCommand.register(dispatcher, siphon);
    }

}
