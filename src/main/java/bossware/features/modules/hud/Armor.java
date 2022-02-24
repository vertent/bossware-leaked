package bossware.features.modules.hud;

import bossware.event.events.Render2DEvent;
import bossware.features.modules.client.HUD;
import bossware.features.setting.Setting;
import bossware.util.ColorUtil;
import bossware.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class Armor extends HudModule {
    public Armor() {
        super("Armor", "Displays your armor durability", false, false, false);
    }

    private final Setting<Boolean> displayOverlay = this.register(new Setting<Boolean>("Overlay", true));
    private final Setting<Boolean> displayText = this.register(new Setting<Boolean>("Text", true));
    private final Setting<Boolean> displayPercentage = this.register(new Setting<Boolean>("Percantage", true, v -> displayText.getValue()));

    @Override
    public void onRender2D(Render2DEvent event) {
        renderArmorHUD();
    }

    public void renderArmorHUD() {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);

            if(displayOverlay.getValue())
                RenderUtil.itemRender.renderItemOverlays(HUD.mc.fontRenderer, is, x, y);

            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.renderer.drawStringWithShadow(s, (float) (x + 19 - 2 - this.renderer.getStringWidth(s)), (float) (y + 9), 16777215);

            int dmg = 0;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            if (displayPercentage.getValue()) {
                dmg = 100 - (int) (red * 100.0f);
            } else {
                dmg = itemDurability;
            }
            if(displayText.getValue())
                this.renderer.drawStringWithShadow(dmg + "", (float) (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0f), (int) (green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    @Override
    protected boolean isPositionCustom() {
        return false;
    }
}
