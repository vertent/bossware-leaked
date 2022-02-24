package bossware.features.gui.components.items.buttons;

import bossware.BossWare;
import bossware.features.gui.PhobosGui;
import bossware.features.modules.client.ClickGui;
import bossware.features.setting.Bind;
import bossware.features.setting.Bind.BindType;
import bossware.features.setting.Setting;
import bossware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton
        extends Button {
    public boolean isListening;
    private final Setting setting;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 13;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? BossWare.colorManager.getColorWithAlpha(((ClickGui) BossWare.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()) : BossWare.colorManager.getColorWithAlpha(((ClickGui) BossWare.moduleManager.getModuleByName("ClickGui")).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));

        if (this.isListening) {
            BossWare.textManager.drawStringWithShadow("Listening...", this.x + 2.3f, this.y - 1.7f - (float) PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        } else {
            BossWare.textManager.drawStringWithShadow(this.setting.getName() + " " + "\u00a77" + this.setting.getValue().toString(), this.x + 2.3f, this.y - 1.7f - (float) PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean listening = this.isListening;
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(listening) {
            //i have severe retardation
            Bind bind = new Bind(mouseButton, BindType.MOUSE);
            this.setting.setValue(bind);
            super.onMouseClick();
        }

        else {
            if (this.isHovering(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

