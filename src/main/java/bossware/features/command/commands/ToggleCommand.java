package bossware.features.command.commands;

import bossware.BossWare;
import bossware.features.command.Command;
import bossware.features.modules.Module;
import bossware.util.TextUtil;

public class ToggleCommand
        extends Command {
    public ToggleCommand() {
        super("toggle", new String[]{"<module>"});
    }

    @Override
    public void execute(String[] commands) {

        if (commands.length == 1) {
            ToggleCommand.sendMessage("Provide the name of the module to toggle!");
            return;
        }
        if (commands.length == 2) {
            Module module = BossWare.moduleManager.getModuleByName(commands[0]);

            if(module != null) {
                module.toggle();
                if(module.isOn()) {
                    ToggleCommand.sendMessage(module.getName() + TextUtil.GREEN + TextUtil.BOLD + " enabled!");
                } else {
                    ToggleCommand.sendMessage(module.getName() + TextUtil.RED + TextUtil.BOLD + " disabled!");
                }

            } else {
                ToggleCommand.sendMessage("Module " + commands[0] + " not found!");
            }

        }
    }
}

