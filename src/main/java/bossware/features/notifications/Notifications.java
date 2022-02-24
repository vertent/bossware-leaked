package bossware.features.notifications;

import bossware.BossWare;
import bossware.util.RenderUtil;
import bossware.util.Timer;
import bossware.features.modules.client.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notifications {
    private final String text;
    private final long disableTime;
    private final float width;
    private final Timer timer = new Timer();

    public Notifications(String text, long disableTime) {
        this.text = text;
        this.disableTime = disableTime;
        this.width = BossWare.moduleManager.getModuleByClass(HUD.class).renderer.getStringWidth(text);
        this.timer.reset();
    }

    public void onDraw(int y) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (this.timer.passedMs(this.disableTime)) {
            BossWare.notificationManager.getNotifications().remove(this);
        }
        RenderUtil.drawRect((float) (scaledResolution.getScaledWidth() - 4) - this.width, y, scaledResolution.getScaledWidth() - 2, y + BossWare.moduleManager.getModuleByClass(HUD.class).renderer.getFontHeight() + 3, 0x75000000);
        BossWare.moduleManager.getModuleByClass(HUD.class).renderer.drawString(this.text, (float) scaledResolution.getScaledWidth() - this.width - 3.0f, y + 2, -1, true);
    }
}

