package dev.bithole.siphon.base;

import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.GetPlayersHandler;
import org.bukkit.Server;

import java.util.List;

public class GetPlayersHandlerImpl extends GetPlayersHandler {

    private final Server server;

    public GetPlayersHandlerImpl(SiphonImpl siphon, Server server) {
        super(siphon);
        this.server = server;
    }

    @Override
    protected List<Player> getPlayerList() {
        return server.getOnlinePlayers().stream().map(player -> new Player(player.getUniqueId(), player.getName())).toList();
    }

}
