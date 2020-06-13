package net.funguscow.foodie.main;

import net.funguscow.foodie.config.pojo.FoodListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.util.Identifier;

import java.io.File;

public class FoodWriter extends ComponentWriter<FoodListing> {

    public FoodWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "foods";
    }

    protected Class<FoodListing> getType(){
        return FoodListing.class;
    }

    protected void write(FoodListing food){
        ConfigConverter.ItemModel model = new ConfigConverter.ItemModel(GsonHelper.firstTextures(food.textures));
        converter.writeJson(converter.itemModels.resolve(food.name + ".json"), model);
        if(food.craft != null && food.ingredients != null) {
            switch (food.craft) {
                case "shapeless": {
                    ConfigConverter.ShapelessRecipe recipe = new ConfigConverter.ShapelessRecipe(food.count,
                            ConfigConverter.modid + ":" + food.name,
                            food.ingredients);
                    converter.writeJson(converter.recipes.resolve(food.name + "_autogen.json"), recipe);
                    break;
                }
                case "smelting": {
                    converter.writeJson(converter.recipes.resolve(food.name + "_autosmelt.json"),
                            new ConfigConverter.CookingRecipe("minecraft:smelting",
                                    ConfigConverter.modid + ":" + food.name,
                                    food.xp,
                                    food.ingredients,
                                    200));
                    converter.writeJson(converter.recipes.resolve(food.name + "_autofire.json"),
                            new ConfigConverter.CookingRecipe("minecraft:campfire_cooking",
                                    ConfigConverter.modid + ":" + food.name,
                                    food.xp,
                                    food.ingredients,
                                    600));
                    converter.writeJson(converter.recipes.resolve(food.name + "_autosmoke.json"),
                            new ConfigConverter.CookingRecipe("minecraft:smoking",
                                    ConfigConverter.modid + ":" + food.name,
                                    food.xp,
                                    food.ingredients,
                                    100));
                    break;
                }
            }
        }
        if(food.tags != null){
            for(String tag : food.tags)
                converter.putItemTag(new Identifier(tag), ConfigConverter.modid + ":" + food.name);
        }
        if(food.english != null)
            converter.translateItem(new Identifier(ConfigConverter.modid, food.name), food.english);
    }

}
