package bossware.manager;

import bossware.features.Feature;
import bossware.features.gui.font.CustomFont;
import bossware.BossWare;
import bossware.util.MathUtil;
import net.minecraft.util.math.MathHelper;
import bossware.features.modules.client.FontMod;
import java.awt.*;
import bossware.util.Timer;

public class TextManager extends Feature
{
    private CustomFont customFont;
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private final Timer idleTimer;
    private boolean idling;

    public TextManager() {
        this.customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
        this.idleTimer = new Timer();
        this.updateResolution();
    }

    public void init(final boolean startup) {
        final FontMod cFont = BossWare.moduleManager.getModuleByClass(FontMod.class);
        try {
            this.setFontRenderer(new Font(cFont.fontName.getValue(), cFont.getFontStyle(), cFont.fontSize.getValue()), cFont.antiAlias.getValue(), cFont.fractionalMetrics.getValue());
        }
        catch (Exception ex) {}
    }

    public void drawStringWithShadow(String text, final float x, final float y, final int color) {
        this.drawString(text, x, y, color, true);
    }

    public float drawString(String text, final float x, final float y, final int color, final boolean shadow) {
        if (BossWare.moduleManager.isModuleEnabled(FontMod.class)) {
            if (shadow) {
                return this.customFont.drawStringWithShadow(text, x, y, color);
            }
            else {
                return this.customFont.drawString(text, x, y, color);
            }
        }
        return TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public void drawRainbowString(String text, float x, float y, int startColor, float factor, boolean shadow) {
        Color currentColor = new Color(startColor);
        float hueIncrement = 1.0f / factor;
        String[] rainbowStrings = text.split("\u00a7.");
        float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
        float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
        float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];
        int currentWidth = 0;
        boolean shouldRainbow = true;
        boolean shouldContinue = false;
        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(MathUtil.clamp(i + 1, 0, text.length() - 1));
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                shouldRainbow = false;
            } else if ((String.valueOf(currentChar) + nextChar).equals("\u00a7+")) {
                shouldRainbow = true;
            }
            if (shouldContinue) {
                shouldContinue = false;
                continue;
            }
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                String escapeString = text.substring(i);
                this.drawString(escapeString, x + (float) currentWidth, y, Color.WHITE.getRGB(), shadow);
                break;
            }
            this.drawString(String.valueOf(currentChar).equals("\u00a7") ? "" : String.valueOf(currentChar), x + (float) currentWidth, y, shouldRainbow ? currentColor.getRGB() : Color.WHITE.getRGB(), shadow);
            if (String.valueOf(currentChar).equals("\u00a7")) {
                shouldContinue = true;
            }
            currentWidth += this.getStringWidth(String.valueOf(currentChar));
            if (String.valueOf(currentChar).equals(" ")) continue;
            currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
            currentHue += hueIncrement;
        }
    }

    public int getStringWidth(final String text) {
        if (BossWare.moduleManager.isModuleEnabled(FontMod.class)) {
            return this.customFont.getStringWidth(text);
        }
        return TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (BossWare.moduleManager.isModuleEnabled(FontMod.class)) {
            final String text = "A";
            return this.customFont.getStringHeight(text);
        }
        return TextManager.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(final Font font, final boolean antiAlias, final boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public CustomFont getCustomFont() { return customFont; }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = TextManager.mc.displayWidth;
        this.scaledHeight = TextManager.mc.displayHeight;
        this.scaleFactor = 1;
        final boolean flag = TextManager.mc.isUnicode();
        int i = TextManager.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        final double scaledWidthD = this.scaledWidth / (double)this.scaleFactor;
        final double scaledHeightD = this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}