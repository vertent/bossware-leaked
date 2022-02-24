package bossware.features.command.commands;

import bossware.features.command.Command;
import bossware.BossWare;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        BossWare.reload();
    }
}

