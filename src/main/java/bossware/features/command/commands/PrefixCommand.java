package bossware.features.command.commands;

import bossware.BossWare;
import bossware.features.command.Command;
import bossware.features.modules.client.ClickGui;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("\u00a7cSpecify a new prefix.");
            return;
        }

        BossWare.commandManager.setPrefix(commands[0]);
        BossWare.moduleManager.getModuleByClass(ClickGui.class).prefix.setValue(commands[0]);
        Command.sendMessage("Prefix set to \u00a7a" + BossWare.commandManager.getPrefix());
    }
}

