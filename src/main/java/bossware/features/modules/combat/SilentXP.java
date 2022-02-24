package bossware.features.modules.combat;

import bossware.features.setting.Setting;
import bossware.features.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class SilentXP extends Module {
    public SilentXP() {
        super("Silent XP", "xps on when u have items in offhand", Category.COMBAT, true, false, false);
    }
    public Setting<Integer> takeoffval = this.register(new Setting<Integer>("TakeOffVal",100,11,100));
    public Setting<Integer> lookPitch = this.register(new Setting<Integer>("Look Pitch", 90, 0, 100));
    public Setting<Boolean> silentRotate = this.register(new Setting<Boolean>("Silent Rotate", false));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay",0, 0, 5));

    private int delay_count;
    int prvSlot;

    @Override
    public void onEnable() {
        delay_count = 0;
    }

    @Override
    public void onTick() {
            int oldPitch = (int)mc.player.rotationPitch;
            prvSlot = mc.player.inventory.currentItem; //TODO add better rotations
            mc.player.connection.sendPacket(new CPacketHeldItemChange(findExpInHotbar()));
            if(!silentRotate.getValue()) {
                mc.player.rotationPitch = lookPitch.getValue();
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, lookPitch.getValue(), true));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            if(!silentRotate.getValue()) {
                mc.player.rotationPitch = oldPitch;
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, oldPitch, true));
            mc.player.inventory.currentItem = prvSlot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
    }

    private int findExpInHotbar() {
        int slot = 0;
        for (int i = 0; i < 9;  i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private ItemStack getArmour(int first) {
        return mc.player.inventoryContainer.getInventory().get(first);
    }

    public Boolean notInInv(Item itemOfChoice) {
        int n;
        n = 0;
        if (itemOfChoice == mc.player.getHeldItemOffhand().getItem()) return true;

        for (int i = 35; i >= 0; i--) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemOfChoice) {
                return true;

            } else if (item != itemOfChoice) {
                n++;
            }
        }
        if (n >= 35) {

            return false;
        }
        return true;
    }
}
