package net.funguscow.foodie.main;

import net.funguscow.foodie.config.pojo.TreeListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TreeWriter extends ComponentWriter<TreeListing> {

    public TreeWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "trees";
    }

    protected Class<TreeListing> getType(){
        return TreeListing.class;
    }

    protected void write(TreeListing tree){
        writeBlockModels(tree);
        writeItemModel(tree);
        writeLootTables(tree);
        writeTags(tree);
        writeTranslations(tree);
    }

    private void writeBlockModels(TreeListing tree){
        if(!tree.trunkName.startsWith("minecraft:")){
            String name = tree.trunkName.split(":")[1] + ".json";
            ConfigConverter.DirBlockState state = ConfigConverter.DirBlockState.axes(tree.trunkName.replace(":",":block/"));
            ConfigConverter.CubeBlockModel model = new ConfigConverter.CubeBlockModel("foodie:column", "side,side_overlay;end,end_overlay", tree.barkTexture, tree.woodTexture);
            ConfigConverter.ItemBlockModel itemModel = new ConfigConverter.ItemBlockModel(tree.trunkName.replace(":", ":block/"));
            converter.generalBlockModels(name, state, model, itemModel);
        }
        if(!tree.leafName.startsWith("minecraft:")){
            String name = tree.leafName.split(":")[1] + ".json";
            ConfigConverter.MonoBlockState state = new ConfigConverter.MonoBlockState(tree.leafName.replace(":",":block/"));
            ConfigConverter.CubeBlockModel model = new ConfigConverter.CubeBlockModel("foodie:cube", "all,overlay", tree.leafTexture);
            ConfigConverter.ItemBlockModel itemModel = new ConfigConverter.ItemBlockModel(tree.leafName.replace(":", ":block/"));
            converter.generalBlockModels(name, state, model, itemModel);
        }
        String saplingName = tree.treeName.split(":")[1] + "_sapling.json";
        ConfigConverter.MonoBlockState state = new ConfigConverter.MonoBlockState(tree.treeName.replace(":", ":block/") + "_sapling");
        List<String> textures = GsonHelper.firstTextures(tree.saplingTexture);
        ConfigConverter.CubeBlockModel cross = new ConfigConverter.CubeBlockModel("foodie:cross", "cross,overlay", textures);
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(textures);
        converter.generalBlockModels(saplingName, state, cross, model);
    }

    private void writeItemModel(TreeListing tree){
        if(tree.fruitName == null || tree.fruitName.startsWith("minecraft:"))
            return;
        String name = tree.fruitName.split(":")[1] + ".json";
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(GsonHelper.firstTextures(tree.fruitTexture));
        converter.writeJson(converter.itemModels.resolve(name), model);
    }

    private void writeLootTables(TreeListing tree){
        if(!tree.trunkName.startsWith("minecraft:")){
            converter.selfLootTable(tree.trunkName);
        }
        if(!tree.treeName.startsWith("minecraft:")){
            converter.selfLootTable(tree.treeName + "_sapling");
        }
        if(!tree.leafName.startsWith("minecraft:")){
            File template = new File(converter.templates, "loot_tables" + File.separator + "leaves.json");
            Path output = converter.lootTables.resolve(tree.leafName.split(":")[1] + ".json");
            String extraDrop = tree.fruitName;
            if(extraDrop == null)
                extraDrop = "minecraft:stick";
            converter.templateFile(template,
                    output,
                    tree.leafName,
                    tree.treeName + "_sapling",
                    extraDrop,
                    tree.baseOdds,
                    tree.baseOdds * 10f/9f,
                    tree.baseOdds * 1.24f,
                    tree.baseOdds * 5f/3f,
                    tree.baseOdds * 5f);
        }
    }

    private void writeTags(TreeListing tree){
        if(!tree.trunkName.startsWith("minecraft:")) {
            converter.putBothTags(new Identifier(tree.logTag), tree.trunkName);
        }
        if(!tree.leafName.startsWith("minecraft:"))
            converter.putBothTags(new Identifier("minecraft", "leaves"), tree.leafName);
        converter.putBothTags(new Identifier("minecraft", "saplings"), tree.treeName + "_sapling");
    }

    private void writeTranslations(TreeListing tree){
        if(tree.treeEnglish != null){
            if(!tree.treeName.startsWith("minecraft:"))
                converter.translateBlock(new Identifier(tree.treeName + "_sapling"), tree.treeEnglish + " Sapling");
            if(!tree.trunkName.startsWith("minecraft:"))
                converter.translateBlock(new Identifier(tree.trunkName), tree.treeEnglish + " Log");
            if(!tree.leafName.startsWith("minecraft:"))
                converter.translateBlock(new Identifier(tree.leafName), tree.treeEnglish + " Leaves");
        }
        if(tree.fruitName != null && tree.fruitEnglish != null)
            converter.translateItem(new Identifier(tree.fruitName), tree.fruitEnglish);
    }

}
