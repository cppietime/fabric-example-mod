package net.funguscow.foodie.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemBlock;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class FoodieAttachedStemBlock extends AttachedStemBlock {

    private final FoodieGourdBlock fgb;

    public FoodieAttachedStemBlock(FoodieGourdBlock gourdBlock, Settings settings) {
        super(gourdBlock, settings);
        fgb = gourdBlock;
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos){
        if(fgb.getMedia() == null)
            return super.canPlantOnTop(floor, view, pos);
        return fgb.getMedia().contains(floor.getBlock());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return neighborState.getBlock() != fgb && facing == state.get(FACING) ? fgb.getStem().getDefaultState().with(StemBlock.AGE, fgb.getStages()) : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Item getSeeds(){
        return fgb.getSeeds();
    }

}
