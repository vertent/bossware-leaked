package bossware.features.modules.hud;

import bossware.BossWare;
import bossware.event.events.Render2DEvent;
import bossware.features.setting.Setting;
import bossware.util.ColorUtil;
import bossware.features.modules.Module;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeatureList extends HudModule {
    private Setting<Position> position = this.register(new Setting<Position>("Position", Position.BOTTOM_RIGHT));
    private Setting<ColorMode> colorMode = this.register(new Setting<ColorMode>("Color Mode", ColorMode.SINGLE));
    private Setting<TextAlign> textAlign = this.register(new Setting<TextAlign>("Text Align", TextAlign.RIGHT, v -> isPositionCustom()));
    private Setting <SortingType> sortingType = this.register(new Setting<SortingType>("Sorting Type", SortingType.DOWN, v -> isPositionCustom()));

    private Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255, v -> colorMode.getValue() == ColorMode.SINGLE));
    private Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255, v -> colorMode.getValue() == ColorMode.SINGLE));
    private Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255, v -> colorMode.getValue() == ColorMode.SINGLE));

    private Setting<Boolean> rainbowRolling = this.register(new Setting<Boolean>("Rainbow Rolling", true, v -> colorMode.getValue() == ColorMode.RAINBOW));
    private Setting<Integer> rollingOffset = this.register(new Setting<Integer>("Rolling Offset", 200, 0, 2000, v -> colorMode.getValue() == ColorMode.RAINBOW
            && rainbowRolling.getValue()));
    private Setting<Integer> rainbowDuration = this.register(new Setting<Integer>("Rainbow Duration", 2000, 100, 10000, v -> colorMode.getValue() ==
            ColorMode.RAINBOW));
    private Setting<Integer> saturation = this.register(new Setting<Integer>("Saturation", 255, 0, 255, v -> colorMode.getValue() == ColorMode.RAINBOW));
    private Setting<Integer> brightness = this.register(new Setting<Integer>("Brigthness", 255, 0, 255, v -> colorMode.getValue() == ColorMode.RAINBOW));

    private Setting<Boolean> shadow = this.register(new Setting<Boolean>("Shadow", true));

    public FeatureList() {
        super("FeatureList", "Lists all the modules currently enabled", false, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        List<Module> modules = new ArrayList<Module>(BossWare.moduleManager.sortedModules);
        int moduleId = 0;

        if(sortingType.getValue() == SortingType.UP && isPositionCustom()) {
            Collections.reverse(modules);
        }

        for(Module module : modules) {
            if(module.isOn() && (module.isDrawn() || module.isSliding())) {
                String text = module.getDisplayName() + "\u00A77" + ((module.getDisplayInfo() != null) ? (" [\u00A7f" + module.getDisplayInfo() + "\u00A77" + "]") : "");
                int color = getColor(moduleId, module);

                float[] pos = getPosition(moduleId, text);

                renderer.drawString(text, pos[0], pos[1], color, shadow.getValue());

                ++moduleId;
            }
        }
    }

    private float[] getPosition(int moduleId, String moduleName) {
        float[] pos = {0.0f, 0.0f};

        switch(position.getValue()) {
            case BOTTOM_RIGHT:
                pos[0] = renderer.scaledWidth - renderer.getStringWidth(moduleName) - 3;
                pos[1] = renderer.scaledHeight - moduleId * (renderer.getFontHeight() + 2) - 10;
                break;
            case TOP_RIGHT:
                pos[0] = renderer.scaledWidth - renderer.getStringWidth(moduleName) - 3;
                pos[1] = moduleId * (renderer.getFontHeight() + 2) + 3;
                break;
            case TOP_LEFT:
                pos[0] = 3;
                pos[1] = moduleId * (renderer.getFontHeight() + 2) + 3;
                break;
            case BOTTOM_LEFT:
                pos[0] = 3;
                pos[1] = renderer.scaledHeight - moduleId * (renderer.getFontHeight() + 2) - 10;
                break;
            case CUSTOM:
                pos[1] = (renderer.scaledHeight * ((float) posY.getValue() / 1000)) - moduleId * (renderer.getFontHeight() + 2);
                if(textAlign.getValue() == TextAlign.RIGHT) {
                    pos[0] = (renderer.scaledWidth * ((float) posX.getValue() / 1000)) - renderer.getStringWidth(moduleName);
                } else {
                    pos[0] = (renderer.scaledWidth * ((float) posX.getValue() / 1000));
                }
                break;
        }

        return pos;
    }

    private int getColor(int moduleId, Module module) {
        //Single color
        if(colorMode.getValue() == ColorMode.SINGLE) {
            return ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue());
        }

        //Colormap
        else if(colorMode.getValue() == ColorMode.COLORMAP) {
            Color color = BossWare.moduleManager.moduleColorMap.get(module);
            return ColorUtil.toRGB(color);
        }

        //Rainbow
        int offset = 0;
        if(rainbowRolling.getValue()) {
            offset = moduleId * rollingOffset.getValue();
        }

        float hue = ((System.currentTimeMillis() + offset) % rainbowDuration.getValue()) / (float)rainbowDuration.getValue();
        float s = (float)saturation.getValue() / 255.0f;
        float b = (float)brightness.getValue() / 255.0f;

        return Color.HSBtoRGB(hue, s, b);
    }

    @Override
    protected boolean isPositionCustom() {
        return position.getValue() == Position.CUSTOM;
    }

    public enum Position {
        BOTTOM_RIGHT,
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT,
        CUSTOM;
    }

    public enum TextAlign {
        LEFT,
        RIGHT;
    }

    public enum ColorMode {
        SINGLE,
        COLORMAP,
        RAINBOW;
    }

    public enum SortingType {
        DOWN,
        UP;
    }
}
