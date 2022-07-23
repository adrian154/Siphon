package dev.bithole.siphon.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.bithole.siphon.SiphonPlugin;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.BroadcastChatHandler;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.*;
import net.md_5.bungee.chat.*;
import org.bukkit.Server;

public class BroadcastChatHandlerImpl extends BroadcastChatHandler {

    // taken from net.md_5.bungee.chat.ComponentSerializer
    private static final Gson gson = new GsonBuilder().
            registerTypeAdapter( BaseComponent.class, new ComponentSerializer() ).
            registerTypeAdapter( TextComponent.class, new TextComponentSerializer() ).
            registerTypeAdapter( TranslatableComponent.class, new TranslatableComponentSerializer() ).
            registerTypeAdapter( KeybindComponent.class, new KeybindComponentSerializer() ).
            registerTypeAdapter( ScoreComponent.class, new ScoreComponentSerializer() ).
            registerTypeAdapter( SelectorComponent.class, new SelectorComponentSerializer() ).
            registerTypeAdapter( Entity.class, new EntitySerializer() ).
            registerTypeAdapter( Text.class, new TextSerializer() ).
            registerTypeAdapter( Item.class, new ItemSerializer() ).
            registerTypeAdapter( ItemTag.class, new ItemTag.Serializer() ).
            create();

    private final SiphonPlugin plugin;
    private final Server server;

    public BroadcastChatHandlerImpl(SiphonPlugin plugin, SiphonImpl siphon, Server server) {
        super(siphon);
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    protected void broadcastMessage(JsonObject object) {
        BaseComponent component = gson.fromJson(object, BaseComponent.class);
        server.spigot().broadcast(component);
    }

}
