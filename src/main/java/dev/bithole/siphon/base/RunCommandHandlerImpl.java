package dev.bithole.siphon.base;

import dev.bithole.siphon.SiphonPlugin;
import dev.bithole.siphon.core.SiphonImpl;
import org.bukkit.Server;

public class RunCommandHandlerImpl extends dev.bithole.siphon.core.base.RunCommandHandler {

    private final SiphonPlugin plugin;
    private final Server server;

    public RunCommandHandlerImpl(SiphonPlugin plugin, SiphonImpl siphon, Server server) {
        super(siphon);
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    protected void runCommand(String command) {
        server.getScheduler().runTask(plugin, () -> server.dispatchCommand(server.getConsoleSender(), command));
    }

}
