package bossware.features.modules.player;

import bossware.BossWare;
import bossware.features.command.Command;
import bossware.features.setting.Setting;
import bossware.util.InventoryUtil;
import bossware.util.Timer;
import bossware.features.modules.Module;
import bossware.features.modules.client.ClickGui;
import bossware.features.modules.client.ServerModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

public class MiddleClick
        extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.FRIEND));
    private final Setting<Integer> expDelay = this.register(new Setting<Integer>("Delay", 0, 0, 1000, v -> this.mode.getValue() == Mode.EXP));
    private final Setting<Boolean> antiFriend = this.register(new Setting<Boolean>("AntiFriend", false, v -> this.mode.getValue() != Mode.FRIEND));
    private final Setting<Boolean> server = this.register(new Setting<Boolean>("Server", false, v -> this.mode.getValue() == Mode.FRIEND));

    private Timer expTimer = new Timer();
    private boolean clicked = false;

    public MiddleClick() {
        super("MiddleClick", "Do stuff when clicking the middle mouse button",
                Category.PLAYER, false, false, false);
    }

    @Override
    public String getDisplayInfo() {
        switch(mode.getValue()) {
            case FRIEND:
                return "Friend";
            case EXP:
                return "Exp";
            default:
                return "Pearl";
        }
    }

    @Override
    public void onTick() {
        if (Mouse.isButtonDown(2)) {
            if (!this.clicked) {
                if(this.mode.getValue() == Mode.PEARL)
                    this.throwPearl();
                else if(this.mode.getValue() == Mode.FRIEND) {
                    this.friendPlayer();
                }
            }

            if(this.mode.getValue() == Mode.EXP) {
                if(expTimer.passedMs(expDelay.getValue())) {
                    this.useExp();
                    expTimer.reset();
                }
            }

            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    private void throwPearl() {
        boolean isHoldingPearlInOffhand;

        if (this.antiFriend.getValue().booleanValue() && isHoveringOverAPlayer()) {
            return;
        }
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean bl = isHoldingPearlInOffhand = MiddleClick.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || isHoldingPearlInOffhand) {
            int oldSlot = MiddleClick.mc.player.inventory.currentItem;
            if (!isHoldingPearlInOffhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            MiddleClick.mc.playerController.processRightClick(MiddleClick.mc.player, MiddleClick.mc.world, isHoldingPearlInOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!isHoldingPearlInOffhand) {
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
        }
    }

    private void friendPlayer() {
        Entity entity = getEntityHoveredOver();
        if (entity instanceof EntityPlayer) {
            if (BossWare.friendManager.isFriend(entity.getName())) {
                BossWare.friendManager.removeFriend(entity.getName());
                Command.sendMessage("\u00a7c" + entity.getName() + "\u00a7r" + " unfriended.");
                if (this.server.getValue().booleanValue() && ServerModule.getInstance().isConnected()) {
                    mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
                    mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "friend del " + entity.getName()));
                }
            } else {
                BossWare.friendManager.addFriend(entity.getName());
                Command.sendMessage("\u00a7b" + entity.getName() + "\u00a7r" + " friended.");
                if (this.server.getValue().booleanValue() && ServerModule.getInstance().isConnected()) {
                    mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
                    mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "friend add " + entity.getName()));
                }
            }
        }
        this.clicked = true;
    }

    private void useExp() {
        boolean isHoldingExpInOffhand;

        if (this.antiFriend.getValue().booleanValue() && isHoveringOverAPlayer()) {
            return;
        }
        int expSlot = InventoryUtil.findHotbarBlock(ItemExpBottle.class);
        boolean bl = isHoldingExpInOffhand = mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (expSlot != -1 || isHoldingExpInOffhand) {
            int oldSlot = mc.player.inventory.currentItem;
            if (!isHoldingExpInOffhand) {
                InventoryUtil.switchToHotbarSlot(expSlot, false);
            }

            if(mc.playerController == null) return;

            mc.playerController.processRightClick(MiddleClick.mc.player, MiddleClick.mc.world, isHoldingExpInOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!isHoldingExpInOffhand) {
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
        }
    }

    private Entity getEntityHoveredOver() {
        RayTraceResult rayTraceResult = mc.objectMouseOver; if(rayTraceResult == null) return null;

        if(rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) {
            return rayTraceResult.entityHit;
        }

        return null;
    }

    private boolean isHoveringOverAPlayer() {
        Entity entityHoveredOver = getEntityHoveredOver();
        if(entityHoveredOver != null) {
            if(entityHoveredOver instanceof  EntityPlayer) {
                return true;
            }
        }

        return false;
    }

    public enum Mode {
        FRIEND,
        PEARL,
        EXP
    }
}

