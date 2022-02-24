package bossware.features.modules.movement;

import bossware.BossWare;
import bossware.event.events.MoveEvent;
import bossware.features.setting.Setting;
import bossware.util.BlockUtil;
import bossware.features.modules.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Anchor
        extends Module {
    public Anchor() {
        super("Anchor", "Stops movement above safe holes", Category.MOVEMENT, true, false, false);
    }

    private Setting<Integer> activateHeight = this.register(new Setting<Integer>("Height", 3, 1, 6));
    private Setting<Integer> minPitch = this.register(new Setting<Integer>("Pitch", 30, 0, 180));
    private Setting<Boolean> center = this.register(new Setting<Boolean>("Center", false));
    private Setting<Boolean> disableInHole = this.register(new Setting<Boolean>("Disable In Hole", false));
    private Setting<Boolean> cancelMotion = this.register(new Setting<Boolean>("Cancel Motion", true));

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if(BlockUtil.isInHole() && disableInHole.getValue()) {
            this.toggle();
            return;
        }

        if(mc.player.rotationPitch > minPitch.getValue()) {
            if (isAboveAHole()) {
                if(center.getValue())
                    centerPlayer();

                mc.player.motionX = 0.0D;
                mc.player.motionZ = 0.0D;

                double blockX = Math.floor(mc.player.posX) + 0.5;
                double blockZ = Math.floor(mc.player.posZ) + 0.5;

                if(cancelMotion.getValue() && Math.abs(mc.player.posX - blockX) < 0.11 && Math.abs(mc.player.posZ - blockZ) < 0.11) {
                    event.setX(0.0);
                    event.setZ(0.0);
                }
            }
        }
    }

    private void centerPlayer() {
        double blockX = Math.floor(mc.player.posX);
        double blockZ = Math.floor(mc.player.posZ);
        BossWare.positionManager.setPositionPacket(blockX + 0.5, mc.player.posY, blockZ + 0.5, mc.player.onGround, true, true);
    }

    private boolean isAboveAHole() {
        double blockX = Math.floor(mc.player.posX);
        double blockZ = Math.floor(mc.player.posZ);
        BlockPos playerBlockPos = new BlockPos(blockX, mc.player.posY, blockZ);
        BlockPos currentBlock = playerBlockPos.down();

        for(int i = 0; i < activateHeight.getValue(); i++) {
            currentBlock = currentBlock.down();

            if(BlockUtil.isBlockAboveAHole(mc.world.getBlockState(currentBlock.up()), currentBlock.up())) {
                return true;
            }
        }

        return false;
    }
}

