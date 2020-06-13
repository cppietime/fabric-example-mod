package net.funguscow.foodie.config;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.blocks.FoodieCropBlock;
import net.funguscow.foodie.blocks.shipping.ShippingBinBlock;
import net.funguscow.foodie.config.pojo.CropListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class CropsConfig extends FoodieConfig<CropListing> {

    public static final CropsConfig Instance = new CropsConfig();

    private CropsConfig() {
        super("crops", CropListing.class);
    }

    public void setup(){
        for(CropListing crop : listings){
            Identifier blockID = new Identifier(FoodieMod.MODID, crop.plantName),
                seedID = new Identifier(crop.seedName),
                produceID = new Identifier(crop.produceName);
            if(crop.cropIsSeed && seedID.getNamespace().equals("minecraft")){
                throw new RuntimeException("Cannot create a new crop whose seed already exists!");
            }
            FoodieCropBlock cropBlock = new FoodieCropBlock(FabricBlockSettings.copy(Blocks.WHEAT), crop);
            Item.Settings seedSettings = new Item.Settings().group(FoodieMod.MAIN_GROUP);
            if(crop.cropIsSeed && crop.hunger + crop.saturation > 0){
                seedSettings = seedSettings.food(new FoodComponent.Builder().hunger(crop.hunger).saturationModifier(crop.saturation).build());
            }
            Item cropSeed = new AliasedBlockItem(cropBlock, seedSettings);
            cropBlock.withSeedsItem(cropSeed);
            blocks.put(blockID, cropBlock);
            items.put(seedID, cropSeed);
            if(!crop.cropIsSeed && !produceID.getNamespace().equals("minecraft")){
                Item.Settings produceSettings = new Item.Settings().group(FoodieMod.MAIN_GROUP);
                if(crop.hunger + crop.saturation > 0){
                    produceSettings = produceSettings.food(new FoodComponent.Builder().hunger(crop.hunger).saturationModifier(crop.saturation).build());
                }
                Item produce = new Item(produceSettings);
                items.put(produceID, produce);
            }
        }
    }

    public void register(){
        super.register();
        final FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder()
                .withRolls(new ConstantLootTableRange(1))
                .withCondition(RandomChanceLootCondition.builder(.125f));

        if(Boolean.parseBoolean(FoodieConfig.getProperty("wild_seeds", "true"))){
            builder.withEntry(ItemEntry.builder(Items.PUMPKIN_SEEDS))
                    .withEntry(ItemEntry.builder(Items.MELON_SEEDS))
                    .withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS));
        }

        for(CropListing crop : listings){
            FoodieCropBlock fcb = (FoodieCropBlock)blocks.get(new Identifier(FoodieMod.MODID, crop.plantName));
            if(crop.media != null)
                fcb.withMedia(crop.media.stream().map(s -> Registry.BLOCK.get(new Identifier(s))).collect(Collectors.toList()));
            if(crop.value > 0){
                String cropName = crop.cropIsSeed ? crop.seedName : crop.produceName;
                ShippingBinBlock.putItem(new Identifier(cropName), crop.value);
            }
            if(crop.findInWild)
                builder.withEntry(ItemEntry.builder(items.get(new Identifier(crop.seedName))));
        }

        LootTableLoadingCallback.EVENT.register((resources, loots, id, supplier, setter) -> {
            if(id.equals(new Identifier("blocks/grass")) || id.equals(new Identifier("blocks/tall_grass")) || id.equals(new Identifier("blocks/fern")))
                supplier.withPool(builder);
        });
    }

    public void registerClient(){
        for(CropListing crop : listings){
            Identifier blockId = new Identifier(FoodieMod.MODID, crop.plantName);
            if(blocks.containsKey(blockId))
               cutout(blockId);
            final List<Integer> blockTints = GsonHelper.colors(crop.cropTexture);
            blockColor(blockTints, blockId);
            Identifier seedId = new Identifier(crop.seedName);
            if(items.containsKey(seedId)){
                final List<Integer> itemTints = GsonHelper.colors(crop.seedLayers);
                itemColor(itemTints, seedId);
            }
            Identifier produceId = new Identifier(crop.produceName);
            if(items.containsKey(produceId)){
                final List<Integer> itemTints = GsonHelper.colors(crop.produceLayers);
                itemColor(itemTints, produceId);
            }
        }
    }

}
