package dev.bithole.siphon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.SiphonImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

import java.io.IOException;
import java.util.Base64;

public class AddClientCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, SiphonImpl siphon) {
        dispatcher.register(Commands.literal("add-client")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("clientName", StringArgumentType.word())
                        .then(Commands.argument("passwordOrKey", StringArgumentType.word())
                                .executes(ctx -> addClient(ctx.getSource(), StringArgumentType.getString(ctx, "clientName"), StringArgumentType.getString(ctx, "passwordOrKey"), new String[] {}, siphon))
                                .then(Commands.argument("permissions", StringArgumentType.greedyString())
                                        .executes(ctx -> addClient(ctx.getSource(), StringArgumentType.getString(ctx, "clientName"), StringArgumentType.getString(ctx, "passwordOrKey"), StringArgumentType.getString(ctx, "permimssions").split("\\s+"), siphon))))));
    }

    private static int addClient(CommandSourceStack source, String name, String passwordOrKey, String[] permissions, SiphonImpl siphon) {

        if(siphon.getConfig().getClient(name) != null) {
            source.sendFailure(new TextComponent("A client with that name exists already"));
            return 1;
        }

        Client client;
        if(passwordOrKey.equals("key")) {

            // generate key
            byte[] key = Client.generateKey();
            client = new Client(name, key);

            // make clickable message
            String keyStr = Base64.getEncoder().encodeToString(key);
            TextComponent copyableKey = new TextComponent("click to copy");
            Style keyStyle = Style.EMPTY
                    .applyFormat(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, keyStr))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(keyStr)));
            source.sendSuccess(new TextComponent("Key: [").append(copyableKey.withStyle(keyStyle)).append("]"), false);

        } else {
            client = new Client(name, passwordOrKey);
        }

        for(String permission: permissions) {
            client.addPermission(permission);
        }

        siphon.getConfig().addClient(client);
        try {
            siphon.getConfig().save();
        } catch(IOException ex) {
            source.sendFailure(new TextComponent("Failed to update config"));
            return 1;
        }

        source.sendSuccess(new TextComponent("Client successfully added!"), true);
        return 0;

    }

}
