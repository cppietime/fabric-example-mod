package net.funguscow.foodie.config.pojo;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.blocks.*;
import net.funguscow.foodie.main.BlockWriter;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class WoodListing {

    public String signType = "oak,00ffff";
    public String logTag;
    public String woodName;
    public String english;
    public List<String> barkTexture,
            woodTexture,
            plankTexture,
            doorTextureTop,
            doorTextureBottom,
            trapdoorTexture,
            strippedTexture;

    public String getWoodName(){
        return woodName;
    }

    public List<String> getBarkTexture() {
        return barkTexture;
    }

    public List<String> getWoodTexture() {
        return woodTexture;
    }

    public List<String> getPlankTexture() {
        return plankTexture;
    }

    public List<String> getDoorTextureTop() {
        return doorTextureTop;
    }

    public List<String> getDoorTextureBottom() {
        return doorTextureBottom;
    }

    public List<String> getTrapdoorTexture() {
        return trapdoorTexture;
    }

    public List<String> getStrippedTexture(){
        return strippedTexture;
    }

    public static Function<WoodListing, String> formatTagGen(String tag){
        return t -> tag.replace("XXX", t.woodName);
    }

    public static final BlockWriter<WoodListing> BUTTON = new BlockWriter<WoodListing>("button", "%1$s_button", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:button", "%1$s_button", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:button_pressed", "%1$s_button_pressed", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new FoodieWoodButton(FabricBlockSettings.copy(Blocks.OAK_BUTTON)), false,
            Arrays.asList(
                    formatTagGen("minecraft:wooden_buttons")
            )),
        DOOR = new BlockWriter<WoodListing>("door", "%1$s_door", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:door_top", "%1$s_door_top", "bottom,bottom_overlay;top,top_overlay", Arrays.asList(
                        WoodListing::getDoorTextureBottom,
                        WoodListing::getDoorTextureTop
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:door_top_rh", "%1$s_door_top_hinge", "bottom,bottom_overlay;top,top_overlay", Arrays.asList(
                        WoodListing::getDoorTextureBottom,
                        WoodListing::getDoorTextureTop
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:door_bottom", "%1$s_door_bottom", "bottom,bottom_overlay", Collections.singletonList(
                        WoodListing::getDoorTextureBottom
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:door_bottom_rh", "%1$s_door_bottom_hinge", "bottom,bottom_overlay", Collections.singletonList(
                        WoodListing::getDoorTextureBottom
                ))
            },
            t -> new FoodieDoor(FabricBlockSettings.copy(Blocks.OAK_DOOR)), true,
            Collections.singletonList(formatTagGen("minecraft:wooden_doors"))),
        FENCE = new BlockWriter<WoodListing>("fence", "%1$s_fence", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_post", "%1$s_fence_post", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_side", "%1$s_fence_side", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new FenceBlock(FabricBlockSettings.copy(Blocks.OAK_FENCE)), false,
            Collections.singletonList(formatTagGen("minecraft:wooden_fences"))),
        FENCE_GATE = new BlockWriter<WoodListing>("fence_gate", "%1$s_fence_gate", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_gate", "%1$s_fence_gate", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_gate_open", "%1$s_fence_gate_open", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_gate_wall", "%1$s_fence_gate_wall", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:fence_gate_wall_open", "%1$s_fence_gate_wall_open", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new FenceGateBlock(FabricBlockSettings.copy(Blocks.OAK_FENCE_GATE)), false,
            Collections.emptyList()),
        PLANKS = new BlockWriter<WoodListing>("planks", "%1$s_planks", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:cube", "%1$s_planks", "all,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new Block(FabricBlockSettings.copy(Blocks.OAK_PLANKS)), false,
            Collections.singletonList(formatTagGen("minecraft:planks"))),
        PRESSURE_PLATE = new BlockWriter<WoodListing>("pressure_plate", "%1$s_pressure_plate", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:pressure_plate_down", "%1$s_pressure_plate_down", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:pressure_plate_up", "%1$s_pressure_plate", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new FoodiePressurePlate(PressurePlateBlock.ActivationRule.EVERYTHING, FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE)),
                false,
                Collections.singletonList(formatTagGen("minecraft:wooden_pressure_plates"))),
        SLAB = new BlockWriter<WoodListing>("slab", "%1$s_slab", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:slab", "%1$s_slab", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:slab_top", "%1$s_slab_top", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new SlabBlock(FabricBlockSettings.copy(Blocks.OAK_SLAB)), false,
                Collections.singletonList(formatTagGen("minecraft:wooden_slabs"))),
        STAIRS = new BlockWriter<WoodListing>("stairs", "%1$s_stairs", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:stairs", "%1$s_stairs", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:inner_stairs", "%1$s_stairs_inner", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:outer_stairs", "%1$s_stairs_outer", "texture,overlay", Collections.singletonList(
                        WoodListing::getPlankTexture
                ))
            },
            t -> new FoodieStairs(Registry.BLOCK.get(new Identifier("foodie", t.woodName + "_planks")).getDefaultState(), FabricBlockSettings.copy(Blocks.OAK_STAIRS)),
                false,
                Collections.singletonList(formatTagGen("minecraft:wooden_stairs"))),
        STRIPPED_LOG = new BlockWriter<WoodListing>("stripped_log", "%1$s_stripped_log", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:column", "%1$s_stripped_log", "end,end_overlay;side,side_overlay", Arrays.asList(
                        WoodListing::getWoodTexture,
                        WoodListing::getStrippedTexture
                ))
            },
            t -> new PillarBlock(FabricBlockSettings.copy(Blocks.STRIPPED_OAK_LOG)), false,
                Collections.singletonList(formatTagGen("%1$s:XXX_logs"))),
        STRIPPED_WOOD = new BlockWriter<WoodListing>("stripped_wood", "%1$s_stripped_wood", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:column", "%1$s_stripped_wood", "end,end_overlay;side,side_overlay", Arrays.asList(
                        WoodListing::getStrippedTexture,
                        WoodListing::getStrippedTexture
                ))
            },
            t -> new PillarBlock(FabricBlockSettings.copy(Blocks.STRIPPED_OAK_LOG)), false,
                Collections.singletonList(formatTagGen("%1$s:XXX_logs"))),
        TRAPDOOR = new BlockWriter<WoodListing>("trapdoor", "%1$s_trapdoor", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:trapdoor_bottom", "%1$s_trapdoor_bottom", "texture,overlay", Collections.singletonList(
                        WoodListing::getTrapdoorTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:trapdoor_open", "%1$s_trapdoor_open", "texture,overlay", Collections.singletonList(
                        WoodListing::getTrapdoorTexture
                )),
                new BlockWriter.ModelWriter<WoodListing>("foodie:trapdoor_top", "%1$s_trapdoor_top", "texture,overlay", Collections.singletonList(
                        WoodListing::getTrapdoorTexture
                ))
            },
            t -> new FoodieTrapdoor(FabricBlockSettings.copy(Blocks.OAK_TRAPDOOR)), true,
                Collections.singletonList(formatTagGen("minecraft:wooden_trapdoors"))),
        WOOD = new BlockWriter<WoodListing>("wood", "%1$s_wood", WoodListing::getWoodName, new BlockWriter.ModelWriter[]{
                new BlockWriter.ModelWriter<WoodListing>("foodie:column", "%1$s_wood", "end,end_overlay;side,side_overlay", Arrays.asList(
                        WoodListing::getBarkTexture,
                        WoodListing::getBarkTexture
                ))
            },
            t -> new PillarBlock(FabricBlockSettings.copy(Blocks.OAK_WOOD)), false,
                Collections.singletonList(formatTagGen("%1$s:XXX_logs")));
        public static final BlockWriter[] BLOCK_WRITERS = {BUTTON, DOOR, FENCE, FENCE_GATE, PLANKS, PRESSURE_PLATE, SLAB, STAIRS, STRIPPED_LOG, STRIPPED_WOOD, TRAPDOOR, WOOD};
}
