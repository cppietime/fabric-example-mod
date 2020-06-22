package net.funguscow.foodie.config;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.blocks.FoodieAttachedStemBlock;
import net.funguscow.foodie.blocks.FoodieGourdBlock;
import net.funguscow.foodie.blocks.FoodieStemBlock;
import net.funguscow.foodie.blocks.shipping.ShippingBinBlock;
import net.funguscow.foodie.config.pojo.StemListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class StemConfig extends FoodieConfig<StemListing> {

    public static final StemConfig Instance = new StemConfig();

    protected StemConfig() {
        super("stems", StemListing.class);
    }

    @Override
    public void setup() {
        try {
            Method compost = ComposterBlock.class.getDeclaredMethod("registerCompostableItem", float.class, ItemConvertible.class);
            compost.setAccessible(true);
            for (StemListing stem : listings) {
                Identifier seedID = new Identifier(stem.seedName),
                        gourdID = new Identifier(stem.gourdName),
                        stemID = new Identifier(stem.stemName),
                        attachedID = new Identifier(stem.stemName.replace(":", ":attached_"));
                FoodieGourdBlock gourdBlock = new FoodieGourdBlock(FabricBlockSettings.copy(Blocks.MELON), stem.maxAge);
                FoodieStemBlock stemBlock = new FoodieStemBlock(gourdBlock, FabricBlockSettings.copy(Blocks.MELON_STEM));
                FoodieAttachedStemBlock attachedStemBlock = new FoodieAttachedStemBlock(gourdBlock, FabricBlockSettings.copy(Blocks.ATTACHED_MELON_STEM));
                Item seeds = new AliasedBlockItem(stemBlock, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                gourdBlock.withSeeds(seeds).withStems(stemBlock, attachedStemBlock);
                Item gourdItem = new BlockItem(gourdBlock, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                items.put(seedID, seeds);
                items.put(gourdID, gourdItem);
                blocks.put(gourdID, gourdBlock);
                blocks.put(stemID, stemBlock);
                blocks.put(attachedID, attachedStemBlock);
                compost.invoke(null, 0.3f, seeds);
                compost.invoke(null, 0.65f, gourdItem);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void register() {
        super.register();
        for(StemListing stem : listings){
            FoodieGourdBlock fgb = (FoodieGourdBlock)blocks.get(new Identifier(stem.gourdName));
            if(stem.media != null)
                fgb.withMedia(stem.media.stream().map(s -> Registry.BLOCK.get(new Identifier(s))).collect(Collectors.toList()));
            if(stem.supports != null)
                fgb.withSupports(stem.supports.stream().map(s -> Registry.BLOCK.get(new Identifier(s))).collect(Collectors.toList()));
            if(stem.value > 0)
                ShippingBinBlock.putItem(new Identifier(stem.gourdName), stem.value);
            if(stem.biomes != null){
                RandomPatchFeatureConfig gourdConfig = new RandomPatchFeatureConfig.Builder(
                        new SimpleBlockStateProvider(fgb.getDefaultState()), new SimpleBlockPlacer()
                )
                        .tries(stem.tries)
                        .canReplace()
                        .cannotProject()
                        .whitelist(fgb.getSupports())
                        .build();
                stem.biomes.stream()
                        .map(b -> Registry.BIOME.get(new Identifier(b)))
                        .forEach(b -> b.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                                Feature.RANDOM_PATCH.configure(gourdConfig)
                                        .createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE
                                                .configure(new CountDecoratorConfig(stem.count)))));
            }
        }
    }

    @Override
    public void registerClient() {
        for(StemListing stem : listings){
            Identifier seedID = new Identifier(stem.seedName),
                    gourdID = new Identifier(stem.gourdName),
                    stemID = new Identifier(stem.stemName),
                    attachedID = new Identifier(stem.stemName.replace(":", ":attached_"));
            final List<Integer> itemTints = stem.seedLayers.stream().map(s -> Integer.parseInt(s.split(",")[1], 16)).collect(Collectors.toList());
            itemColor(itemTints, seedID);
            if(stem.gourdTexture.size() > 1)
                cutout(gourdID);
            if(stem.gourdTexture.stream().anyMatch(s -> s.contains(","))) {
                final List<Integer> gourdTints = GsonHelper.colors(stem.gourdTexture);
                blockColor(gourdTints, gourdID);
                itemColor(gourdTints, gourdID);
            }
            if(stem.stemTexture.contains(",")){
                final int color = Integer.parseInt(stem.stemTexture.split(",")[1], 16);
                ColorProviderRegistry.BLOCK.register((state, view, pos, tint) -> color, blocks.get(stemID));
            }
            if(stem.attachedTexture.contains(",")){
                final int color = Integer.parseInt(stem.attachedTexture.split(",")[1], 16);
                ColorProviderRegistry.BLOCK.register((state, view, pos, tint) -> color, blocks.get(attachedID));
            }
            cutout(stemID);
            cutout(attachedID);
        }
    }
}
