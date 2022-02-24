package bossware.features.command.commands;

import bossware.BossWare;
import bossware.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        BossWare.unload(true);
    }
}

