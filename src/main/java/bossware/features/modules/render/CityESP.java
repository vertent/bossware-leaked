package bossware.features.modules.render;

import bossware.event.events.Render3DEvent;
import bossware.features.setting.Setting;
import bossware.util.BlockUtil;
import bossware.util.RenderUtil;
import bossware.features.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CityESP
        extends Module {

    private final Setting<Boolean> self = this.register(new Setting<Boolean>("Self", true));
    private final Setting<Boolean> others = this.register(new Setting<Boolean>("Others", true));

    private final Setting<Integer> distance = this.register(new Setting<Integer>("Distance", 6, 1, 30));
    private final Setting <Boolean> box = this.register(new Setting<Boolean>("Box", true));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("Line width", 1.0f, 0.1f, 5.0f, v -> outline.getValue()));

    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("Box Alpha", 200, 0, 255, v -> box.getValue()));

    private List<BlockPos> playerBlockPositions = new ArrayList<BlockPos>();

    public CityESP() {
        super("CityESP", "Highlights obsidian/echest blocks that players in safe holes are surrounded with.", Category.RENDER,
                false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        playerBlockPositions.clear();
        for(Entity entity : mc.world.loadedEntityList) {

            if(entity instanceof EntityPlayer) {
                if(entity.entityId == mc.player.entityId && !self.getValue()) {
                    return;
                }

                if(entity.entityId != mc.player.entityId && !others.getValue()) {
                    return;
                }

                if(mc.player.getDistance(entity) <= distance.getValue()) {
                    BlockPos playerBlockPos = new BlockPos(entity.posX, entity.posY, entity.posZ);

                    if(BlockUtil.isInHole(entity) && !areCityBlocksAlreadyDrawn(playerBlockPos)) {
                        playerBlockPositions.add(playerBlockPos);

                        List<BlockPos> blockPositions = getSurroundingBreakableBlocks(entity);
                        for(BlockPos blockPos : blockPositions) {
                            RenderUtil.drawBoxESP(blockPos, new Color(red.getValue(), green.getValue(), blue.getValue()), false, new Color(0, 0, 0, 0),
                                    lineWidth.getValue(), outline.getValue(), box.getValue(), boxAlpha.getValue(), false);
                        }

                    }
                }
            }
        }
    }

    private List<BlockPos> getSurroundingBreakableBlocks(Entity entity) {
        List<BlockPos> surroundingBreakableBlocks = new ArrayList<BlockPos>();
        BlockPos entityBlockPos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        IBlockState entityBlockState = mc.world.getBlockState(entityBlockPos);

        //Basically, echests are retarded because they are smaller than normal blocks, so we have to do this
        if(entityBlockState.getBlock() == Blocks.ENDER_CHEST) entityBlockPos = entityBlockPos.up();

        for(BlockPos blockPos : BlockUtil.getSurroundingBlocks(entityBlockPos)) {
            IBlockState blockState = mc.world.getBlockState(blockPos);
            if(blockState.getBlock() != Blocks.BEDROCK && blockState.getBlock() != Blocks.AIR) {
                surroundingBreakableBlocks.add(blockPos);
            }
        }

        return surroundingBreakableBlocks;
    }

    private boolean areCityBlocksAlreadyDrawn(BlockPos blockPos) {
        //Horrible function name but whatever
        //This is to prevent two/more CityESP's being drawn on top of each other if more players are in a single hole
        for(BlockPos pos : playerBlockPositions) {
            if(blockPos.getX() == pos.getX() && blockPos.getY() == pos.getY() && blockPos.getZ() == pos.getZ()) {
                return true;
            }
        }

        return false;
    }
}
