package dev.bithole.siphon;

import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.SiphonImpl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class AddClientCommand implements CommandExecutor {

    private final SiphonImpl siphon;

    public AddClientCommand(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(args.length < 2) {
            return false;
        }

        String name = args[0];
        List<String> permissions = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));

        if(siphon.getConfig().getClient(name) != null) {
            commandSender.sendMessage(ChatColor.RED + "A client with that name already exists");
            return true;
        }

        Client client;
        if(args[1].equals("key")) {
            byte[] key = Client.generateKey();
            client = new Client(name, key);
            String keyStr = Base64.getEncoder().encodeToString(key);
            TextComponent copyableKey = new TextComponent("click to copy");
            copyableKey.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, keyStr));
            copyableKey.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(keyStr)));
            copyableKey.setColor(ChatColor.GREEN);
            commandSender.spigot().sendMessage(new ComponentBuilder("Key: [").append(copyableKey).append("]").create());
            commandSender.sendMessage(ChatColor.RED + "Make sure to copy the key as you can never access it again!");
        } else {
            client = new Client(name, args[1]);
        }

        permissions.forEach(permission -> client.addPermission(permission));
        siphon.getConfig().addClient(client);

        try {
            siphon.getConfig().save();
        } catch(IOException ex) {
            commandSender.sendMessage(ChatColor.RED + "Failed to update config");
            // FIXME: log exception
        }

        commandSender.sendMessage(ChatColor.GREEN + "Client successfully added!");

        return true;

    }
}
