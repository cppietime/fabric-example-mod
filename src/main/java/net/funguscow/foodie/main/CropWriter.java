package net.funguscow.foodie.main;

import net.funguscow.foodie.config.pojo.CropListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;

public class CropWriter extends ComponentWriter<CropListing> {

    public CropWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "crops";
    }

    protected Class<CropListing> getType(){
        return CropListing.class;
    }

    protected void write(CropListing crop){
        writeSeedModel(crop);
        writeProduceModel(crop);
        writeBlockModels(crop);
        writeLootTable(crop);
        writeTranslations(crop);
    }

    private void writeSeedModel(CropListing crop){
        if(crop.seedName.startsWith("minecraft:"))
            return;
        Path writeTo = converter.itemModels.resolve(crop.seedName.split(":")[1] + ".json");
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(GsonHelper.firstTextures(crop.seedLayers));
        converter.writeJson(writeTo, model);
        converter.putItemTag(new Identifier(ConfigConverter.modid, "seeds"), crop.seedName);
    }

    private void writeProduceModel(CropListing crop){
        if(crop.seedName.startsWith("minecraft:") || crop.cropIsSeed)
            return;
        Path writeTo = converter.itemModels.resolve(crop.produceName.split(":")[1] + ".json");
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(GsonHelper.firstTextures(crop.produceLayers));
        converter.writeJson(writeTo, model);
    }

    private void writeBlockModels(CropListing crop){
        String prefix = crop.cropTexture.get(0).split(",")[0];
        String prefix_overlay = crop.cropTexture.size() > 1 ? crop.cropTexture.get(1).split(",")[0] : null;
        for(int i = 0; i <= crop.maxAge; i++){
            Path writeTo = converter.blockModels.resolve(crop.plantName + "_stage" + i + ".json");
            ConfigConverter.CropBlockModel model;
            String texture = prefix + "_stage" + i;
            if(prefix_overlay != null) {
                String overlay = prefix_overlay + "_stage" + i;
                model = new ConfigConverter.CropBlockModel("crop", texture, overlay);
            }
            else
                model = new ConfigConverter.CropBlockModel("crop", texture);
            converter.writeJson(writeTo, model);
        }
        ConfigConverter.GrowingBlockState states = new ConfigConverter.GrowingBlockState(ConfigConverter.modid + ":block/" + crop.plantName, crop.maxAge);
        Path writeTo = converter.blockStates.resolve(crop.plantName + ".json");
        converter.writeJson(writeTo, states);
    }

    private void writeLootTable(CropListing crop){
        Path writeTo = converter.lootTables.resolve(crop.plantName + ".json");
        ConfigConverter.LootTable table = new ConfigConverter.LootTable(crop);
        converter.writeJson(writeTo, table);
    }

    private void writeTranslations(CropListing crop){
        if(crop.english == null)
            return;
        if(!crop.seedName.startsWith("minecraft:")){
            Identifier id = new Identifier(crop.seedName);
            String name = crop.english;
            if(!crop.cropIsSeed)
                name += " Seeds";
            converter.translateItem(id, name);
        }
        Identifier id = new Identifier(crop.plantName);
        converter.translateBlock(id, crop.english);
        if(!crop.cropIsSeed && !crop.produceName.startsWith("minecraft:")){
            id = new Identifier(crop.produceName);
            converter.translateItem(id, crop.english);
        }
    }

}
