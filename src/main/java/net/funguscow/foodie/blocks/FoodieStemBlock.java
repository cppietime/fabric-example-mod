package net.funguscow.foodie.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

import java.util.Random;

/**
 * Custom gourd-growing block
 */
public class FoodieStemBlock extends StemBlock {

    private final FoodieGourdBlock fgb;

    public FoodieStemBlock(FoodieGourdBlock gourdBlock, Settings settings){
        super(gourdBlock, settings);
        fgb = gourdBlock;

    }

    public void growFruit(ServerWorld world, BlockPos pos, Random random){
        for(int attempt = 0; attempt < 4; attempt++) {
            Direction direction = Direction.Type.HORIZONTAL.random(random);
            BlockPos blockPos = pos.offset(direction);
            Block block = world.getBlockState(blockPos.down()).getBlock();
            if (world.getBlockState(blockPos).isAir() && canFruitOnTop(block)) {
                world.setBlockState(blockPos, this.fgb.getDefaultState());
                world.setBlockState(pos, this.fgb.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                return;
            }
        }
    }

    public boolean canFruitOnTop(Block block){
        return fgb.getSupports().contains(block);
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos){
        if(fgb.getMedia() == null)
            return super.canPlantOnTop(floor, view, pos);
        return fgb.getMedia().contains(floor.getBlock());
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            float f = FoodieCropBlock.getAvailableMoisture(this, world, pos);
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                int i = state.get(AGE);
                if (i < fgb.getStages()) {
                    state = state.with(AGE, i + 1);
                    world.setBlockState(pos, state, 2);
                } else {
                    growFruit(world, pos, random);
                }
            }

        }
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if(state.get(AGE) == fgb.getStages()){
            growFruit(world, pos, random);
        }
        else {
            int i = Math.min(fgb.getStages(), state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
            BlockState blockState = state.with(AGE, i);
            world.setBlockState(pos, blockState, 2);
            if (i == fgb.getStages()) {
                blockState.scheduledTick(world, pos, world.random);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Item getPickItem(){
        return fgb.getSeeds();
    }

}
