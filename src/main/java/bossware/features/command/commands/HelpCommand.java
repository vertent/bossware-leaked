package bossware.features.command.commands;

import bossware.BossWare;
import bossware.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("commands");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("You can use following commands: ");
        for (Command command : BossWare.commandManager.getCommands()) {
            HelpCommand.sendMessage(BossWare.commandManager.getPrefix() + command.getName());
        }
    }
}

