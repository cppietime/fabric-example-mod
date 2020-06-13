package net.funguscow.foodie.utils;

import net.minecraft.block.Block;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

import java.util.List;

public class FoodieTreeFeatureConfig {

    public static BranchedTreeFeatureConfig genConfig(Block trunk, Block leaves, int height){
        return new BranchedTreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(trunk.getDefaultState()),
            new SimpleBlockStateProvider(leaves.getDefaultState()),
            new BlobFoliagePlacer(2, 0))
            .baseHeight(height)
            .heightRandA(2)
            .foliageHeight(3)
            .noVines()
            .build();
    }
}
