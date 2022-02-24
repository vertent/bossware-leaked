package bossware.features.modules.client;

import bossware.BossWare;
import bossware.event.events.ClientEvent;
import bossware.features.command.Command;
import bossware.features.gui.PhobosGui;
import bossware.features.setting.Setting;
import bossware.features.modules.Module;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    public Setting <Boolean> blurEffect = this.register(new Setting <>("Blur", true));
    public Setting<Float> outlineThickness = this.register(new Setting<Float>("LineThickness", 1.0f, 0.5f, 5.0f, v -> outline.getValue()));
    public Setting<Boolean> moduleDescription = this.register(new Setting<Boolean>("Description", true));
    public Setting<String> prefix = this.register(new Setting<String>("Prefix", ".").setRenderName(true));
    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("Alpha", 180, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("HoverAlpha", 240, 0, 255));
    public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false));
    public Setting<Float> fov = this.register(new Setting<Object>("Fov", Float.valueOf(150.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f), v -> this.customFov.getValue()));
    public Setting<Boolean> openCloseChange = this.register(new Setting<Boolean>("Open/Close", false));
    public Setting<Boolean> devSettings = this.register(new Setting<Boolean>("DevSettings", false));
    public Setting<Integer> topRed = this.register(new Setting<Object>("TopRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.devSettings.getValue()));
    public Setting<Integer> topGreen = this.register(new Setting<Object>("TopGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.devSettings.getValue()));
    public Setting<Integer> topBlue = this.register(new Setting<Object>("TopBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.devSettings.getValue()));
    public Setting<Integer> topAlpha = this.register(new Setting<Object>("TopAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.devSettings.getValue()));

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        setBind(Keyboard.KEY_P);
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                BossWare.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to \u00a7a" + BossWare.commandManager.getPrefix());
            }
            BossWare.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public
    void onEnable() {
        mc.displayGuiScreen(new PhobosGui());
        if (this.blurEffect.getValue()) {
            ClickGui.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }

    @Override
    public void onLoad() {
        if (this.colorSync.getValue().booleanValue()) {
            BossWare.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), this.hoverAlpha.getValue());
        } else {
            BossWare.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        }
        BossWare.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public
    void onTick() {
        if (! (ClickGui.mc.currentScreen instanceof PhobosGui)) {
            this.disable();
            if (mc.entityRenderer.getShaderGroup() != null)
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof PhobosGui) {
            mc.displayGuiScreen(null);
        }
    }
}

