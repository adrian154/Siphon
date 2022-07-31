package dev.bithole.siphon.base;

import dev.bithole.siphon.SiphonMod;
import dev.bithole.siphon.core.SiphonImpl;

public class RunCommandHandlerImpl extends dev.bithole.siphon.core.base.RunCommandHandler {

    private final SiphonMod mod;

    public RunCommandHandlerImpl(SiphonMod mod, SiphonImpl siphon) {
        super(siphon);
        this.mod = mod;
    }

    @Override
    protected void runCommand(String command) {
        mod.getServer().getCommands().performCommand(mod.getServer().createCommandSourceStack(), command);
    }

}
