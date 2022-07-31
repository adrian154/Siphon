package dev.bithole.siphon.base;

import dev.bithole.siphon.SiphonMod;
import dev.bithole.siphon.core.SiphonImpl;
import dev.bithole.siphon.core.base.GetPlayersHandler;

import java.util.List;

public class GetPlayersHandlerImpl extends GetPlayersHandler {

    private final SiphonMod mod;

    public GetPlayersHandlerImpl(SiphonMod mod, SiphonImpl siphon) {
        super(siphon);
        this.mod = mod;
    }

    @Override
    protected List<Player> getPlayerList() {
        return mod.getServer().getPlayerList().getPlayers().stream().map(player -> new Player(player.getUUID(), player.getScoreboardName())).toList();
    }

}
