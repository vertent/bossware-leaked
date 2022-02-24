package bossware.features.modules.misc;

import bossware.BossWare;
import bossware.features.command.Command;
import bossware.features.setting.Setting;
import bossware.util.TextUtil;
import bossware.features.modules.Module;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundList;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;

public class DonkeyNotify
        extends Module {
    public DonkeyNotify() {
        super("DonkeyNotify", "Screams at you when you come across a donkey", Category.MISC, false, false, false);
    }

    private static DonkeyNotify instance;
    private final Setting<Boolean> sendInChat = this.register(new Setting<Boolean>("Chat", true));
    private final Setting<Boolean> notification = this.register(new Setting<Boolean>("Notification", true));
    private final Setting<Boolean> displayCoords = this.register(new Setting<Boolean>("Coords", true));

    public void onDonkeySpawn(Entity donkey) {
        if(fullNullCheck() || BossWare.notificationManager == null || donkey == null) return;

        String message = "Found a donkey!";

        if(displayCoords.getValue()) {
            double donkeyX = Math.round(donkey.posX * 100) / 100;
            double donkeyY = Math.round(donkey.posY * 100) / 100;
            double donkeyZ = Math.round(donkey.posZ * 100) / 100;

            message += (" (" + String.valueOf(donkeyX) + " (" + String.valueOf(donkeyY) + " " + String.valueOf(donkeyZ) + ")");
        }

        if(sendInChat.getValue())
            mc.player.sendMessage(new Command.ChatMessage("[" + TextUtil.RED + "DonkeyNotify" + TextUtil.RESET + "] " + message));

        if(notification.getValue())
            BossWare.notificationManager.addNotification(message, 2000L);
    }

    public static DonkeyNotify getInstance() {
        if(instance == null) {
            instance = new DonkeyNotify();
        }

        return instance;
    }
}
