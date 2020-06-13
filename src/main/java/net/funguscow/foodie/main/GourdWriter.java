package net.funguscow.foodie.main;

import net.funguscow.foodie.config.pojo.StemListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;

public class GourdWriter extends ComponentWriter<StemListing> {

    public GourdWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "stems";
    }

    protected Class<StemListing> getType(){
        return StemListing.class;
    }

    protected void write(StemListing stem){
        writeSeedModel(stem);
        writeBlockModels(stem);
        writeLootTables(stem);
        writeRecipes(stem);
        writeTranslations(stem);
    }

    private void writeSeedModel(StemListing stem){
        if(stem.seedName.startsWith("minecraft:"))
            return;
        Path writeTo = converter.itemModels.resolve(stem.seedName.split(":")[1] + ".json");
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(GsonHelper.firstTextures(stem.seedLayers));
        converter.writeJson(writeTo, model);
    }

    private void writeBlockModels(StemListing stem){
        if(!stem.stemName.startsWith("minecraft:")) {
            ConfigConverter.GrowingBlockState stemState = new ConfigConverter.GrowingBlockState(stem.stemName.replace(":", ":block/"), stem.maxAge);
            Path writeTo = converter.blockStates.resolve(stem.stemName.split(":")[1] + ".json");
            converter.writeJson(writeTo, stemState);
            for (int i = 0; i <= stem.maxAge; i++) {
                ConfigConverter.StemBlockModel model = new ConfigConverter.StemBlockModel(i, stem, false);
                writeTo = converter.blockModels.resolve(stem.stemName.split(":")[1] + "_stage" + i + ".json");
                converter.writeJson(writeTo, model);
            }

            ConfigConverter.DirBlockState state = ConfigConverter.DirBlockState.fourWay(stem.stemName.replace(":", ":block/attached_"));
            ConfigConverter.StemBlockModel attached = new ConfigConverter.StemBlockModel(0, stem, true);
            converter.generalBlockModels("attached_" + stem.stemName.split(":")[1] + ".json", state, attached, null);
        }
        if(!stem.gourdName.startsWith("minecraft:")){
            ConfigConverter.MonoBlockState state = new ConfigConverter.MonoBlockState(stem.gourdName.replace(":", ":block/"));
            ConfigConverter.CubeBlockModel model = new ConfigConverter.CubeBlockModel("foodie:cube", "all,overlay", stem.gourdTexture);
            ConfigConverter.ItemBlockModel itemBlockModel = new ConfigConverter.ItemBlockModel(stem.gourdName.replace(":", ":block/"));
            converter.generalBlockModels(stem.gourdName.split(":")[1] + ".json", state, model, itemBlockModel);
        }
    }

    private void writeLootTables(StemListing stem){
        ConfigConverter.LootTable forStem = new ConfigConverter.LootTable(stem.seedName);
        converter.writeJson(converter.lootTables.resolve(stem.stemName.split(":")[1] + ".json"), forStem);
        converter.writeJson(converter.lootTables.resolve("attached_" + stem.stemName.split(":")[1] + ".json"), forStem);
        converter.selfLootTable(stem.gourdName);
    }

    private void writeRecipes(StemListing stem){
        if(!stem.seedFromGourd)
            return;
        ConfigConverter.ShapelessRecipe recipe = new ConfigConverter.ShapelessRecipe(1, stem.seedName, "item:" + stem.gourdName);
        converter.writeJson(converter.recipes.resolve(stem.seedName.split(":")[1] + "_from_" + stem.gourdName.split(":")[1] + ".json"), recipe);
    }

    private void writeTranslations(StemListing stem){
        if(stem.english == null)
            return;
        if(!stem.gourdName.startsWith("minecraft:"))
            converter.translateBlock(new Identifier(stem.gourdName), stem.english);
        if(!stem.stemName.startsWith("minecraft:"))
            converter.translateBlock(new Identifier(stem.stemName), stem.english + " Stem");
        if(!stem.seedName.startsWith("minecraft:"))
            converter.translateItem(new Identifier(stem.seedName), stem.english + " Seeds");
    }

}
