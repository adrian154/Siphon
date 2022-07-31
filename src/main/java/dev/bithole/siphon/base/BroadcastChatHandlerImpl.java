package dev.bithole.siphon.base;

import com.google.gson.JsonObject;
import dev.bithole.siphon.SiphonMod;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.BroadcastChatHandler;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class BroadcastChatHandlerImpl extends BroadcastChatHandler {

    private final SiphonMod mod;

    public BroadcastChatHandlerImpl(SiphonMod mod, SiphonImpl siphon) {
        super(siphon);
        this.mod = mod;
    }

    @Override
    protected void broadcastMessage(JsonObject object) {
        Component component = Component.Serializer.fromJson(object);
        mod.getServer().getPlayerList().broadcastMessage(component, ChatType.SYSTEM, UUID.randomUUID());
    }

}
