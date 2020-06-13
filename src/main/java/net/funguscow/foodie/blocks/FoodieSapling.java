package net.funguscow.foodie.blocks;

import net.minecraft.block.*;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Collection;

public class FoodieSapling extends SaplingBlock {

    private Collection<Block> media;

    public FoodieSapling(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
    }

    public FoodieSapling withMedia(Collection<Block> media){
        this.media = media;
        return this;
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos){
        if(media == null)
            return super.canPlantOnTop(floor, view, pos);
        return media.contains(floor.getBlock());
    }
}
