package net.funguscow.foodie.main;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlockWriter<T> {

    public String blockstateTemplate;
    public String blockTemplate;
    public boolean seeThru;
    public Function<T, String> name;
    public ModelWriter<T>[] models;
    public Function<T, Block> newBlock;
    public List<Function<T, String>> tags;

    public static class ModelWriter<T> {
        public String parentModel;
        public String name;
        public String keyList;
        public List<Function<T, List<String>>> textures;

        public ModelWriter(String parent, String name, String keys, List<Function<T, List<String>>>textures){
            parentModel = parent;
            this.name = name;
            keyList = keys;
            this.textures = textures;
        }
    }

    public BlockWriter(String template,
                       String block,
                       Function<T, String> name,
                       ModelWriter<T>[] models,
                       Function<T, Block> blockGen,
                       boolean seeThru,
                       List<Function<T, String>> tags){
        blockstateTemplate = template;
        blockTemplate = block;
        this.name = name;
        this.models = models;
        newBlock = blockGen;
        this.seeThru = seeThru;
        this.tags = tags;
    }

    public String getBlockName(T obj){
        return String.format(blockTemplate, name.apply(obj));
    }

    public boolean layered(T obj){
        if(seeThru)
            return true;
        for(ModelWriter<T> writer : models){
            List<String>[] textures = writer.textures.stream().map(t -> t.apply(obj)).toArray(ArrayList[]::new);
            for(List<String> texture : textures){
                if(texture.size() > 1)
                    return true;
            }
        }
        return false;
    }

    public void write(ConfigConverter converter, T obj){
        String id = ConfigConverter.modid + ":" + getBlockName(obj);
        File source = new File(converter.templates, "blockstates" + File.separator + blockstateTemplate + ".json");
        Path dest = converter.blockStates.resolve(getBlockName(obj) + ".json");
        converter.templateFile(source, dest, name.apply(obj), ConfigConverter.modid);
        for(ModelWriter<T> writer : models){
            List<String>[] textures = writer.textures.stream().map(t -> t.apply(obj)).toArray(ArrayList[]::new);
            ConfigConverter.CubeBlockModel model = new ConfigConverter.CubeBlockModel(writer.parentModel, writer.keyList, textures);
            dest = converter.blockModels.resolve(String.format(writer.name, name.apply(obj)) + ".json");
            converter.writeJson(dest, model);
        }
        dest = converter.itemModels.resolve(getBlockName(obj) + ".json");
        String firstModel = String.format(models[0].name, name.apply(obj));
        ConfigConverter.ItemBlockModel itemModel = new ConfigConverter.ItemBlockModel(ConfigConverter.modid + ":block/" + firstModel);
        converter.writeJson(dest, itemModel);
        dest = converter.lootTables.resolve(getBlockName(obj) + ".json");
        ConfigConverter.LootTable lootTable = new ConfigConverter.LootTable(id);
        converter.writeJson(dest, lootTable);
        for(Function<T, String> tag : tags){
            converter.putBothTags(new Identifier(String.format(tag.apply(obj), ConfigConverter.modid)), id);
        }
    }

}
