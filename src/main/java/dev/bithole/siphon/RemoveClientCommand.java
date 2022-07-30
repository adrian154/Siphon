package dev.bithole.siphon;

import dev.bithole.siphon.core.Client;
import dev.bithole.siphon.core.SiphonImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveClientCommand implements CommandExecutor {

    private final SiphonImpl siphon;

    public RemoveClientCommand(SiphonImpl siphon) {
        this.siphon = siphon;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(args.length != 1) {
            return false;
        }

        Client client = siphon.getConfig().getClient(args[0]);
        if(client != null) {
            siphon.getConfig().removeClient(client);
            sender.sendMessage(ChatColor.RED + "Client was removed");
        } else {
            sender.sendMessage(ChatColor.RED + "There is no client called " + args[0]);
        }

        return true;
    }

}
