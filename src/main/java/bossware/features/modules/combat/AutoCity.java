package bossware.features.modules.combat;

import bossware.BossWare;
import bossware.event.events.Render3DEvent;
import bossware.features.command.Command;
import bossware.features.setting.Setting;
import bossware.util.InventoryUtil;
import bossware.util.RenderUtil;
import bossware.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;


public class AutoCity extends Module {

    public AutoCity() {
        super("AutoCity", "breaks ppl's cities", Category.COMBAT, false, false, false);
    }

    private final Setting<Double> range = this.register(new Setting<Double>("Range",5.0,1.0,6.0));
    private final Setting<Boolean> toggle = this.register(new Setting<Boolean>("Toggle", false));
    private final Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("Auto Switch", false));
    private final Setting<Boolean> switchBack = this.register(new Setting<Boolean>("Switch Back", false, v -> autoSwitch.getValue()));
    private final Setting<Boolean> announceWhenUse = this.register(new Setting<Boolean>("Announce", true));

    private final Setting <Boolean> render = this.register(new Setting<Boolean>("Render", true));
    private final Setting <Boolean> box = this.register(new Setting<Boolean>("Box", true, v -> render.getValue()));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true, v -> render.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("Line width", 1.0f, 0.1f, 5.0f, v -> outline.getValue() && render.getValue()));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255, v -> render.getValue()));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255, v -> render.getValue()));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255, v -> render.getValue()));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("Box Alpha", 200, 0, 255, v -> box.getValue() && render.getValue()));

    private boolean firstRun;
    private int oldSlot = -1;
    private BlockPos mineTarget;
    private EntityPlayer closestTarget;

    @Override
    public void onEnable() {
        if (AutoCity.mc.player == null) {
            this.toggle();
            return;
        }
        this.firstRun = true;
    }
    @Override
    public void onDisable() {
        if (AutoCity.mc.player == null) {
            return;
        }
        Command.sendMessage(ChatFormatting.RED.toString() + "Disabled" + ChatFormatting.RESET.toString() + " AutoCity");
    }

    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }

        if(!firstRun && mineTarget != null) {
            Block targetBlock = mc.world.getBlockState(mineTarget).getBlock();
            if(targetBlock != null) {
                if (targetBlock == Blocks.AIR || mc.player.getDistance(mineTarget.getX(), mineTarget.getY(), mineTarget.getZ()) > range.getValue()) {
                    mineTarget = null;
                    if (switchBack.getValue() && oldSlot != -1) {
                        InventoryUtil.switchToHotbarSlot(oldSlot, false);
                        oldSlot = -1;
                    }
                    if (toggle.getValue()) {
                        this.toggle();
                    } else {
                        firstRun = true;
                    }
                } else {
                    return;
                }
            }
        }

        this.findClosestTarget();
        if (this.closestTarget == null) {
            if (this.firstRun) {
                this.firstRun = false;
                if (this.announceWhenUse.getValue().booleanValue()) {
                    Command.sendMessage(ChatFormatting.GREEN.toString() + "Enabled" + ChatFormatting.RESET.toString() + " AutoCity, dumbass theres no one to city");
                }
            }
            if(toggle.getValue()) {
                this.toggle();
            }
            return;
        }
        if (this.firstRun && this.mineTarget != null) {
            this.firstRun = false;
            if (this.announceWhenUse.getValue().booleanValue()) {
                Command.sendMessage("Mining " + ChatFormatting.AQUA.toString() + this.closestTarget.getName());
            }
        }
        this.findCityBlock();

        if (this.mineTarget != null) {
            if(autoSwitch.getValue()) {
                int newSlot = -1;
                oldSlot = mc.player.inventory.currentItem;

                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = AutoCity.mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemPickaxe)) continue;
                    newSlot = i;
                    break;
                }
                if (newSlot != -1) {
                    AutoCity.mc.player.inventory.currentItem = newSlot;
                }
            }

            AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
            AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
        } else {
            if(toggle.getValue()) {
                this.toggle();
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(render.getValue()) {
            if(mineTarget != null) {
                if(mc.world.getBlockState(mineTarget).getBlock() != Blocks.AIR) {
                    RenderUtil.drawBoxESP(mineTarget, new Color(red.getValue(), green.getValue(), blue.getValue()), false, new Color(0, 0, 0, 0),
                            lineWidth.getValue(), outline.getValue(), box.getValue(), boxAlpha.getValue(), false);
                }
            }
        }
    }

    public BlockPos findCityBlock() {
        Double dist = this.range.getValue();
        Vec3d vec = this.closestTarget.getPositionVector();
        if (AutoCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.add(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.add(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.add(0.0, 0.0, -1.0));
            if (this.canBreak(targetX)) {
                this.mineTarget = targetX;
            }
            if (!this.canBreak(targetX) && this.canBreak(targetXMinus)) {
                this.mineTarget = targetXMinus;
            }
            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && this.canBreak(targetZ)) {
                this.mineTarget = targetZ;
            }
            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && this.canBreak(targetZMinus)) {
                this.mineTarget = targetZMinus;
            }
            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && !this.canBreak(targetZMinus) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                this.mineTarget = null;
            }
        }

        if(mineTarget != null) {
            if(mc.world.getBlockState(mineTarget).getBlock() == Blocks.AIR) {
                mineTarget = null;
            }
            else if(mc.player.getDistance(mineTarget.getX(), mineTarget.getY(),mineTarget.getZ()) > range.getValue()) {
                mineTarget = null;
            }
        }

        return this.mineTarget;
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = AutoCity.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)AutoCity.mc.world, pos) != -1.0f;
    }

    private void findClosestTarget() {
        this.closestTarget = null;
        for (EntityPlayer target : AutoCity.mc.world.playerEntities) {
            if (target == AutoCity.mc.player || BossWare.friendManager.isFriend(target.getName()) || !AutoCity.isLiving((Entity)target) || target.getHealth() <= 0.0f) continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (AutoCity.mc.player.getDistance((Entity)target) >= AutoCity.mc.player.getDistance((Entity)this.closestTarget)) continue;
            this.closestTarget = target;
        }
    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }
}
