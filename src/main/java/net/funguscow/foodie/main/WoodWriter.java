package net.funguscow.foodie.main;

import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.config.pojo.WoodListing;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WoodWriter extends ComponentWriter<WoodListing> {

    private static final String[] RECIPES = {"boat", "button", "door", "fence", "fence_gate", "planks", "pressure_plate", "sign", "slab", "stairs", "trapdoor", "wood"};

    public WoodWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "woods";
    }

    protected Class<WoodListing> getType(){
        return WoodListing.class;
    }

    protected void write(WoodListing wood){
        for(BlockWriter<WoodListing> writer : WoodListing.BLOCK_WRITERS){
            writer.write(converter, wood);
            Identifier id = new Identifier(ConfigConverter.modid, writer.getBlockName(wood));
            String english = wood.english + Stream.of(writer.blockstateTemplate.split("_"))
                    .map(s -> " " + s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.joining());
            converter.translateBlock(id, english);
        }

        /* Create sign */
        File signTemplate = new File(converter.templates, "blockstates" + File.separator + "sign.json");
        converter.templateFile(signTemplate,
                converter.blockStates.resolve(wood.woodName + "_sign.json"),
                wood.woodName, ConfigConverter.modid);
        converter.templateFile(signTemplate,
                converter.blockStates.resolve(wood.woodName + "_wall_sign.json"),
                wood.woodName, ConfigConverter.modid);
        String sign = wood.signType.split(",")[0];
        String standName = wood.woodName + "_sign",
                wallName = wood.woodName + "_wall_sign";
        ConfigConverter.CubeBlockModel blockModel = new ConfigConverter.CubeBlockModel(null, "particle", Collections.singletonList(wood.plankTexture.get(0)));
        ConfigConverter.ItemModel itemModel = new ConfigConverter.ItemModel(Collections.singletonList("minecraft:item/" + sign + "_sign"));
        ConfigConverter.LootTable table = new ConfigConverter.LootTable(ConfigConverter.modid + ":" + standName);
        converter.writeJson(converter.blockModels.resolve(wood.woodName + "_sign.json"), blockModel);
        converter.writeJson(converter.itemModels.resolve(wood.woodName + "_sign.json"), itemModel);
        converter.writeJson(converter.lootTables.resolve(wood.woodName + "_sign.json"), table);
        converter.writeJson(converter.lootTables.resolve(wood.woodName + "_wall_sign.json"), table);
        converter.putBlockTag(new Identifier("minecraft:standing_signs"), ConfigConverter.modid + ":" + standName);
        converter.putBlockTag(new Identifier("minecraft:wall_signs"), ConfigConverter.modid + ":" + wallName);
        converter.putItemTag(new Identifier("minecraft:signs"), ConfigConverter.modid + ":" + standName);
        if(wood.english != null) {
            converter.translateBlock(new Identifier(ConfigConverter.modid, standName), wood.english + " Sign");
            converter.translateBlock(new Identifier(ConfigConverter.modid, wallName), wood.english + " Sign");
            converter.translateItem(new Identifier(ConfigConverter.modid, standName), wood.english + " Sign");
        }

        /* Write tags */
        converter.putBothTags(new Identifier("minecraft:logs"), "#" + wood.logTag);
        for(String recipe : RECIPES){
            converter.templateFile(new File(converter.templates, "recipes" + File.separator + recipe + ".json"),
                    converter.recipes.resolve(wood.woodName + "_" + recipe + ".json"), wood.woodName, ConfigConverter.modid);

        }
    }

}
