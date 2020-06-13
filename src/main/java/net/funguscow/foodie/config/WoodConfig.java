package net.funguscow.foodie.config;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.config.pojo.WoodListing;
import net.funguscow.foodie.injection.SignTypeInject;
import net.funguscow.foodie.injection.WoodStrippings;
import net.funguscow.foodie.main.BlockWriter;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

/**
 * Configures/registers custom mod wood types
 */
public class WoodConfig extends FoodieConfig<WoodListing> {

    public static final WoodConfig Instance = new WoodConfig();

    /**
     * Read from woods.json
     */
    public WoodConfig(){
        super("woods", WoodListing.class);
    }

    @Override
    public void setup() {
        for(WoodListing wood : listings){

            /* Perform registered block writings */
            for(BlockWriter<WoodListing> woodWriter : WoodListing.BLOCK_WRITERS){
                Identifier blockID = new Identifier(FoodieMod.MODID, woodWriter.getBlockName(wood));
                Block block = woodWriter.newBlock.apply(wood);
                blocks.put(blockID, block);
                Item blockItem = new BlockItem(block, new Item.Settings().group(FoodieMod.MAIN_GROUP));
                items.put(blockID, blockItem);
            }

            /* Try to create sign */
            SignType type = SignTypeInject.addType(wood.signType);
            SignBlock stand = new SignBlock(FabricBlockSettings.copy(Blocks.OAK_SIGN), type);
            WallSignBlock wall = new WallSignBlock(FabricBlockSettings.copy(Blocks.OAK_WALL_SIGN), type);
            SignItem item = new SignItem(new Item.Settings().group(FoodieMod.MAIN_GROUP), stand, wall);
            blocks.put(new Identifier(FoodieMod.MODID, wood.woodName + "_sign"), stand);
            blocks.put(new Identifier(FoodieMod.MODID, wood.woodName + "_wall_sign"), wall);
            items.put(new Identifier(FoodieMod.MODID, wood.woodName + "_sign"), item);
        }
    }

    @Override
    public void register() {
        super.register();

        /* Register wood strippings (i.e. stripped log, stripped wood) */
        for(WoodListing wood: listings) {
            Block strippedLog = blocks.get(new Identifier(FoodieMod.MODID, wood.woodName + "_stripped_log")),
                    strippedWood = blocks.get(new Identifier(FoodieMod.MODID, wood.woodName + "_stripped_wood")),
                    log = Registry.BLOCK.get(new Identifier(FoodieMod.MODID, wood.woodName + "_log")),
                    trunk = blocks.get(new Identifier(FoodieMod.MODID, wood.woodName + "_wood"));
            WoodStrippings.addStrip(log, strippedLog);
            WoodStrippings.addStrip(trunk, strippedWood);
        }
    }

    @Override
    public void registerClient() {
        for(WoodListing wood : listings){

            /* For each registered block, if it has multiple layers, register seethru */
            for(BlockWriter<WoodListing> writer : WoodListing.BLOCK_WRITERS){
                Identifier id = new Identifier(FoodieMod.MODID, writer.getBlockName(wood));
                if(writer.layered(wood)) {
                    cutout(id);
                }
                final List<Integer> colors = GsonHelper.colors(writer.models[0].textures.get(0).apply(wood));//.stream().map(s -> Integer.parseInt(s.split(",")[1], 16)).collect(Collectors.toList());
                blockColor(colors, id);
                itemColor(colors, id);
            }

            /* Register sign colors */
            final List<Integer> colors = Collections.singletonList(Integer.parseInt(wood.signType.split(",")[1], 16));
            blockColor(colors, new Identifier(FoodieMod.MODID, wood.woodName + "_sign"));
            blockColor(colors, new Identifier(FoodieMod.MODID, wood.woodName + "_wall_sign"));
            itemColor(colors, new Identifier(FoodieMod.MODID, wood.woodName + "_sign"));
        }
    }
}
