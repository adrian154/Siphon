package dev.bithole.siphon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.SiphonImpl;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

import java.io.IOException;

public class RemoveClientCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, SiphonImpl siphon) {
        dispatcher.register(Commands.literal("remove-client")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("clientName", StringArgumentType.word())
                        .executes(ctx -> removeClient(ctx.getSource(), StringArgumentType.getString(ctx, "clientName"), siphon))));
    }

    private static int removeClient(CommandSourceStack source, String name, SiphonImpl siphon) {
        Client client = siphon.getConfig().getClient(name);
        if(client != null) {
            try {
                siphon.getConfig().removeClient(client);
                siphon.getConfig().save();
                source.sendSuccess(new TextComponent("Client was removed"), true);
                return 0;
            } catch(IOException ex) {
                source.sendFailure(new TextComponent("Failed to update config"));
                return 1;
            }
        }
        source.sendFailure(new TextComponent("There is no client called " + name));
        return 1;
    }

}