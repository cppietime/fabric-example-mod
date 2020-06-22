package net.funguscow.foodie.config;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.blocks.FoodieSapling;
import net.funguscow.foodie.blocks.shipping.ShippingBinBlock;
import net.funguscow.foodie.config.pojo.TreeListing;
import net.funguscow.foodie.generation.FoodieSimpleSaplingGenerator;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class TreeConfig extends FoodieConfig<TreeListing> {

    public static final TreeConfig Instance = new TreeConfig();

    private final Map<Identifier, ConfiguredFeature<BranchedTreeFeatureConfig, ?>> saplings;

    protected TreeConfig() {
        super("trees", TreeListing.class);
        saplings = new HashMap<>();
    }

    @Override
    public void setup() {
        try {
            Method compost = ComposterBlock.class.getDeclaredMethod("registerCompostableItem", float.class, ItemConvertible.class);
            compost.setAccessible(true);
            for (TreeListing tree : listings) {
                Identifier trunkID = new Identifier(tree.trunkName),
                        leafID = new Identifier(tree.leafName),
                        saplingID = new Identifier(tree.treeName + "_sapling");
                Block trunk, leaves;
                if (!trunkID.getNamespace().equals("minecraft")) {
                    trunk = new LogBlock(MaterialColor.WOOD, FabricBlockSettings.copy(Blocks.OAK_LOG));
                    blocks.put(trunkID, trunk);
                    Item trunkItem = new BlockItem(trunk, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                    items.put(trunkID, trunkItem);
                } else
                    trunk = Registry.BLOCK.get(trunkID);
                if (!leafID.getNamespace().equals("minecraft")) {
                    leaves = new LeavesBlock(FabricBlockSettings.copy(Blocks.OAK_LEAVES));
                    blocks.put(leafID, leaves);
                    Item leavesItem = new BlockItem(leaves, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                    items.put(leafID, leavesItem);
                    compost.invoke(null, 0.3f, leavesItem);
                } else
                    leaves = Registry.BLOCK.get(leafID);
                FoodieSimpleSaplingGenerator saplingGenerator = new FoodieSimpleSaplingGenerator(trunk, leaves, tree.height);
                saplings.put(saplingID, saplingGenerator.createTreeFeature(new Random(), false));
                Block sapling = new FoodieSapling(saplingGenerator, FabricBlockSettings.copy(Blocks.OAK_SAPLING));
                blocks.put(saplingID, sapling);
                Item saplingItem = new BlockItem(sapling, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                items.put(saplingID, saplingItem);
                compost.invoke(null, 0.5f, saplingItem);
                if (tree.fruitName != null && !tree.fruitName.startsWith("minecraft:")) {
                    Identifier fruitID = new Identifier(tree.fruitName);
                    Item.Settings settings = new Item.Settings().group(FoodieMod.MAIN_GROUP);
                    if (tree.hunger + tree.saturation > 0) {
                        settings = settings.food(new FoodComponent.Builder().hunger(tree.hunger).saturationModifier(tree.saturation).build());
                    }
                    Item fruit = new Item(settings);
                    items.put(fruitID, fruit);
                    compost.invoke(null, 0.65f, fruit);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void register() {
        super.register();
        for(TreeListing tree : listings){
            Identifier saplingID = new Identifier(tree.treeName + "_sapling");
            ConfiguredFeature<BranchedTreeFeatureConfig, ?> feature = saplings.get(saplingID);
            List<String> biomeNames = tree.biomes;
            Set<Biome> biomes;
            if(biomeNames != null)
                biomes = biomeNames.stream().map(name -> Registry.BIOME.get(new Identifier(name))).collect(Collectors.toSet());
            else
                biomes = Biome.BIOMES;
            biomes.forEach(biome -> biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES,
                    feature.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(tree.density, tree.extraChance, tree.extraDensity)))));
            if(tree.value > 0 && tree.fruitName != null)
                ShippingBinBlock.putItem(new Identifier(tree.fruitName), tree.value);
        }
    }

    @Override
    public void registerClient() {
        for(TreeListing tree : listings){
            Identifier trunkID = new Identifier(tree.trunkName),
                    leafID = new Identifier(tree.leafName),
                    saplingID = new Identifier(tree.treeName + "_sapling");
            if(!trunkID.getNamespace().equals("minecraft")){
                final List<Integer> colors = GsonHelper.colors(tree.barkTexture);
                blockColor(colors, trunkID);
                itemColor(colors, trunkID);
                if(tree.woodTexture.size() > 1 || tree.barkTexture.size() > 1)
                    cutout(trunkID);
            }
            if(!leafID.getNamespace().equals("minecraft")){
                final List<Integer> colors = GsonHelper.colors(tree.leafTexture);
                blockColor(colors, leafID);
                itemColor(colors, leafID);
                if(tree.leafTexture.size() > 1)
                    cutout(leafID);
            }
            {
                final List<Integer> colors = GsonHelper.colors(tree.saplingTexture);
                blockColor(colors, saplingID);
                itemColor(colors, saplingID);
                cutout(saplingID);
            }
            if(tree.fruitName != null && !tree.fruitName.startsWith("minecraft:")){
                Identifier fruitID = new Identifier(tree.fruitName);
                final List<Integer> colors = GsonHelper.colors(tree.fruitTexture);
                itemColor(colors, fruitID);
            }
        }
    }
}
