package bossware.features.modules.render;

import bossware.BossWare;
import bossware.event.events.Render3DEvent;
import bossware.features.setting.Setting;
import bossware.util.RenderUtil;
import bossware.util.RotationUtil;
import bossware.features.modules.Module;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public class HoleESP
        extends Module {
    private static HoleESP INSTANCE = new HoleESP();
    public final Setting<Float> maxDistance = this.register(new Setting<Float>("Distance", 40.0f, 3.0f, 140.0f));
    private final Setting<Boolean> distanceFade = this.register(new Setting<Boolean>("Distance Fade", true));
    private final Setting<Integer> fadeStrength = this.register(new Setting<Integer>("Fade Strength", 7, 1, 10, v -> distanceFade.getValue()));
    public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    public Setting<Boolean> gradientBox = this.register(new Setting<Object>("GradientBox", Boolean.valueOf(false), v -> this.box.getValue()));
    public Setting<Boolean> pulseAlpha = this.register(new Setting<Object>("PulseAlpha", Boolean.valueOf(false), v -> this.gradientBox.getValue()));
    public Setting<Boolean> pulseOutline = this.register(new Setting<Object>("PulseOutline", Boolean.valueOf(true), v -> this.gradientBox.getValue()));
    public Setting<Boolean> invertGradientBox = this.register(new Setting<Object>("InvertGradientBox", Boolean.valueOf(false), v -> this.gradientBox.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    public Setting<Boolean> gradientOutline = this.register(new Setting<Object>("GradientOutline", Boolean.valueOf(false), v -> this.outline.getValue()));
    public Setting<Boolean> invertGradientOutline = this.register(new Setting<Object>("InvertGradientOutline", Boolean.valueOf(false), v -> this.gradientOutline.getValue()));
    public Setting<Double> height = this.register(new Setting<Double>("Height", 0.0, -2.0, 2.0));
    public Setting<Boolean> safeColor = this.register(new Setting<Boolean>("SafeColor", false));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.outline.getValue()));
    //private final Setting<Integer> holes = this.register(new Setting<Integer>("Holes", 3, 1, 500));
    private final Setting<Integer> minPulseAlpha = this.register(new Setting<Object>("MinPulse", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(255), v -> this.pulseAlpha.getValue()));
    private final Setting<Integer> maxPulseAlpha = this.register(new Setting<Object>("MaxPulse", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> this.pulseAlpha.getValue()));
    private final Setting<Integer> pulseSpeed = this.register(new Setting<Object>("PulseSpeed", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(50), v -> this.pulseAlpha.getValue()));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    private final Setting<Integer> safeRed = this.register(new Setting<Object>("SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeGreen = this.register(new Setting<Object>("SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeBlue = this.register(new Setting<Object>("SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> safeAlpha = this.register(new Setting<Object>("SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> safecRed = this.register(new Setting<Object>("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecGreen = this.register(new Setting<Object>("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecBlue = this.register(new Setting<Object>("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private final Setting<Integer> safecAlpha = this.register(new Setting<Object>("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private boolean pulsing = false;
    private boolean shouldDecrease = false;
    private int pulseDelay = 0;
    private int currentPulseAlpha;
    private int currentAlpha = 0;

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        int drawnHoles = 0;
        if (!this.pulsing && this.pulseAlpha.getValue().booleanValue()) {
            Random rand = new Random();
            this.currentPulseAlpha = rand.nextInt(this.maxPulseAlpha.getValue() - this.minPulseAlpha.getValue() + 1) + this.minPulseAlpha.getValue();
            this.pulsing = true;
            this.shouldDecrease = false;
        }
        if (this.pulseDelay == 0) {
            if (this.pulsing && this.pulseAlpha.getValue().booleanValue() && !this.shouldDecrease) {
                ++this.currentAlpha;
                if (this.currentAlpha >= this.currentPulseAlpha) {
                    this.shouldDecrease = true;
                }
            }
            if (this.pulsing && this.pulseAlpha.getValue().booleanValue() && this.shouldDecrease) {
                --this.currentAlpha;
            }
            if (this.currentAlpha <= 0) {
                this.pulsing = false;
                this.shouldDecrease = false;
            }
            ++this.pulseDelay;
        } else {
            ++this.pulseDelay;
            if (this.pulseDelay == 51 - this.pulseSpeed.getValue()) {
                this.pulseDelay = 0;
            }
        }
        if (!this.pulseAlpha.getValue().booleanValue() || !this.pulsing) {
            this.currentAlpha = 0;
        }
        for (BlockPos pos : BossWare.holeManager.getSortedHoles()) {
            double distanceToHole = mc.player.getDistanceSq(pos);

            if (distanceToHole > maxDistance.getValue()) break;
            if (pos.equals(new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.ownHole.getValue().booleanValue() || !RotationUtil.isInFov(pos))
                continue;

            int safeAplhaValue = safeAlpha.getValue();
            int safecAlphaValue = safecAlpha.getValue();
            int alphaValue = alpha.getValue();
            int cAlphaValue = cAlpha.getValue();
            int boxAlphaValue = boxAlpha.getValue();

            if(distanceFade.getValue()) {
                double fadeAfter = ((10.0 - (double)fadeStrength.getValue()) / 10.0) * maxDistance.getValue();
                if(distanceToHole > fadeAfter) {
                    double percentage = ((maxDistance.getValue() - distanceToHole)) / (maxDistance.getValue() - fadeAfter);
                    safeAplhaValue *= percentage;
                    safecAlphaValue *= percentage;
                    alphaValue *= percentage;
                    cAlphaValue *= percentage;
                    currentAlpha *= percentage;
                    boxAlphaValue *= percentage;
                }
            }

            if (this.safeColor.getValue().booleanValue() && BossWare.holeManager.isSafe(pos)) {
                RenderUtil.drawBoxESP(pos, new Color(this.safeRed.getValue(), this.safeGreen.getValue(), this.safeBlue.getValue(), safeAplhaValue), this.customOutline.getValue(), new Color(this.safecRed.getValue(), this.safecGreen.getValue(), this.safecBlue.getValue(), safecAlphaValue), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), boxAlphaValue, true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), alphaValue), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), cAlphaValue), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), boxAlphaValue, true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
            }
            ++drawnHoles;
        }
    }
}

