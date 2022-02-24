package bossware.features.modules.hud;

import bossware.BossWare;
import bossware.event.events.Render2DEvent;
import bossware.features.setting.Setting;
import bossware.util.ColorUtil;
import bossware.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Watermark extends HudModule {
    private Setting<WatermarkType> watermarkType = this.register(new Setting<WatermarkType>("Watermark", WatermarkType.BOSSWARE_CC));
    private Setting<String> customWatermark = this.register(new Setting<String>("Text", "gonky.club", v -> watermarkType.getValue() == WatermarkType.CUSTOM));

    private Setting<Boolean> displayVersion = this.register(new Setting<Boolean>("Version", true));
    private Setting<Boolean> displayIgn = this.register(new Setting<Boolean>("IGN", true));
    private Setting<Boolean> displayFps = this.register(new Setting<Boolean>("FPS", false));
    private Setting<Boolean> displayTps = this.register(new Setting<Boolean>("TPS", false));
    private Setting<Boolean> displayPing = this.register(new Setting<Boolean>("Ping", false));
    private Setting<Boolean> alwaysUseCustomFont = this.register(new Setting<Boolean>("Custom Font", true));

    private Setting<ColorMode> colorMode = this.register(new Setting<ColorMode>("Color Mode", ColorMode.SOLID));

    private Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255, v -> colorMode.getValue() == ColorMode.SOLID));
    private Setting<Integer> green = this.register(new Setting<Integer>("Green", 34, 0, 255, v -> colorMode.getValue() == ColorMode.SOLID));
    private Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 17, 0, 255, v -> colorMode.getValue() == ColorMode.SOLID));

    private Setting<Integer> rainbowDuration = this.register(new Setting<Integer>("Rainbow Duration", 2000, 100, 10000, v -> colorMode.getValue() ==
            ColorMode.RAINBOW));
    private Setting<Integer> saturation = this.register(new Setting<Integer>("Saturation", 255, 0, 255, v -> colorMode.getValue() == ColorMode.RAINBOW));
    private Setting<Integer> brightness = this.register(new Setting<Integer>("Brigthness", 255, 0, 255, v -> colorMode.getValue() == ColorMode.RAINBOW));


    public Watermark() {
        super("Watermark", "Displays the client's name and some other info", false, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String watermarkText = getWatermarkText();
        float[] pos = getPosition(watermarkText);

        int watermarkWidth = 0;
        int fontHeight = 0;

        if(alwaysUseCustomFont.getValue()) {
            watermarkWidth = renderer.getCustomFont().getStringWidth(watermarkText);
            fontHeight = renderer.getCustomFont().getStringHeight("A");
        } else {
            watermarkWidth = renderer.getStringWidth(watermarkText);
            fontHeight = renderer.getFontHeight();
        }

        RenderUtil.drawRect(pos[0], pos[1], watermarkWidth + 6, fontHeight + 7, 0xff000000);
        RenderUtil.drawLine(pos[0], pos[1], pos[0] + watermarkWidth + 4, pos[1], 2f, getLineColor());

        if (alwaysUseCustomFont.getValue()) {
            renderer.getCustomFont().drawString(watermarkText, pos[0] + 2, pos[1] + 3, 0xffffff, false);
        } else {
            renderer.drawString(watermarkText, pos[0] + 2, pos[1] + 3, 0xffffff, false);
        }
    }

    private String getWatermarkText() {
        String watermarkText;

        switch(watermarkType.getValue()) {
            case BOSSWARE_CC:
                watermarkText = "Bossware.cc";
                break;
            case BOSSWARE:
                watermarkText = "BossWare";
                break;
            default:
                watermarkText = customWatermark.getValue();
                break;
        }

        if(displayVersion.getValue()) {
            watermarkText += " " + BossWare.MODVER;
        }

        if(displayIgn.getValue()) {
            watermarkText += " | " + mc.player.getGameProfile().getName();
        }

        if(displayFps.getValue()) {
            watermarkText += " | FPS: " + Minecraft.getDebugFPS();
        }

        if(displayTps.getValue()) {
            watermarkText += " | TPS: " + Math.round(BossWare.serverManager.getTPS() * 100) / 100;
        }

        if(displayPing.getValue()) {
            watermarkText += " | Ping: " + BossWare.serverManager.getPing();
        }

        return watermarkText;
    }

    private float[] getPosition(String watermark) {
        //don't question why it's a seperate function
        float[] pos = { 2.0f, 2.0f };

        return pos;
    }

    private int getLineColor() {
        if(colorMode.getValue() == ColorMode.SOLID) {
            return ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue());
        }

        float hue = ((System.currentTimeMillis()) % rainbowDuration.getValue()) / (float)rainbowDuration.getValue();
        float sat = (float)saturation.getValue() / 255.0f;
        float bright = (float)brightness.getValue() / 255.0f;

        return Color.HSBtoRGB(hue, sat, bright);
    }

    @Override
    protected boolean isPositionCustom() {
        return false;
    }

    public enum WatermarkType {
        BOSSWARE_CC,
        BOSSWARE,
        CUSTOM;
    }

    private enum ColorMode {
        SOLID,
        RAINBOW;
    }
}
