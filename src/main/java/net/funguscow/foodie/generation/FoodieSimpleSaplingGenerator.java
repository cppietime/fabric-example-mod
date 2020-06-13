package net.funguscow.foodie.generation;

import net.funguscow.foodie.utils.FoodieTreeFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class FoodieSimpleSaplingGenerator extends SaplingGenerator {

    private final BranchedTreeFeatureConfig config;

    public FoodieSimpleSaplingGenerator(Block trunk, Block leaves, int height){
        config = FoodieTreeFeatureConfig.genConfig(trunk, leaves, height);
    }

    @Override
    public ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return Feature.NORMAL_TREE.configure(config);
    }
}
