package bossware.features.modules.hud;

import bossware.event.events.Render2DEvent;
import bossware.features.setting.Setting;
import bossware.util.RotationUtil;
import bossware.util.TextUtil;

public class Coordinates extends HudModule {
    private Setting<Position> position = this.register(new Setting<Position>("Position", Position.BOTTOM_LEFT));
    private Setting<Boolean> displayDirection = this.register(new Setting<Boolean>("Direction", true));
    private Setting<Boolean> multiLine = this.register(new Setting<Boolean>("Multi Line", true, v -> displayDirection.getValue()));
    private Setting<Boolean> alignLeft = this.register(new Setting<Boolean>("Align Left", true, v -> position.getValue() == Position.CUSTOM && displayDirection.getValue()
            && multiLine.getValue()));
    private Setting<Boolean> textShadow = this.register(new Setting<Boolean>("Shadow", true));

    public Coordinates() {
        super("Coords", "Displays coordinated and direction", false, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String coords = getText();
        float[] pos = getPos(coords);

        if(displayDirection.getValue() && multiLine.getValue()) {
            String direction = getDirectionText();

            if(position.getValue() == Position.BOTTOM_RIGHT || (position.getValue() == Position.CUSTOM && !alignLeft.getValue())) {
                renderer.drawString(direction, pos[0] + renderer.getStringWidth(coords) - renderer.getStringWidth(direction), pos[1] - renderer.getFontHeight() - 2,
                        0xffffff, textShadow.getValue());
            } else {
                renderer.drawString(direction, pos[0], pos[1] - renderer.getFontHeight() - 2, 0xffffff, textShadow.getValue());
            }
        }

        renderer.drawString(coords, pos[0], pos[1], 0xffffff, textShadow.getValue());
    }

    private String getText() {
        float x = Math.round(mc.player.posX * 100.0f) / 100.0f;
        float y = Math.round(mc.player.posY * 100.0f) / 100.0f;
        float z = Math.round(mc.player.posZ * 100.0f) / 100.0f;

        boolean isInNether = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equalsIgnoreCase("Hell");
        float netherMultiplier = isInNether ? 8.0f : 0.125f;

        float netherX = Math.round(x * netherMultiplier * 100f) / 100f;
        float netherZ = Math.round(z * netherMultiplier * 100f) / 100f;

        String text = TextUtil.DARK_BLUE + "XYZ " + TextUtil.WHITE + x + ", " + y + ", " + String.valueOf(z) + TextUtil.GRAY + " [" + TextUtil.WHITE +
                netherX + ", " + netherZ + TextUtil.GRAY + "]" + TextUtil.RESET;

        if(displayDirection.getValue() && !multiLine.getValue())
            return getDirectionText() + " " + text;

        return text;
    }

    private float[] getPos(String text) {
        float[] pos = {0.0f, 0.0f};

        switch(position.getValue()) {
            case BOTTOM_LEFT:
                pos[0] = 2.0f;
                pos[1] = renderer.scaledHeight - 10;
                break;

            case BOTTOM_RIGHT:
                pos[0] = renderer.scaledWidth - 3 - renderer.getStringWidth(text);
                pos[1] = renderer.scaledHeight - 10;
                break;
            case CUSTOM:
                pos[0] = renderer.scaledWidth * ((float) posX.getValue() / 1000);
                pos[1] = renderer.scaledHeight * ((float) posY.getValue() / 1000);
                break;
        }

        return pos;
    }

    private String getDirectionText() {
        int direction = RotationUtil.getDirection4D();
        switch(direction) {
            case 0:
                return TextUtil.DARK_BLUE + "South" + TextUtil.GRAY + " (" + TextUtil.WHITE + "+Z" + TextUtil.GRAY + ")";
            case 1:
                return TextUtil.DARK_BLUE + "West" + TextUtil.GRAY + " (" + TextUtil.WHITE + "-X" + TextUtil.GRAY + ")";
            case 2:
                return TextUtil.DARK_BLUE + "North" + TextUtil.GRAY + " (" + TextUtil.WHITE + "-Z" + TextUtil.GRAY + ")";
            default:
                return TextUtil.DARK_BLUE + "East" + TextUtil.GRAY + " (" + TextUtil.WHITE + "+X" + TextUtil.GRAY + ")";
        }
    }

    @Override
    protected boolean isPositionCustom() {
        return position.getValue() == Position.CUSTOM;
    }

    public enum Position {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CUSTOM;
    }
}
