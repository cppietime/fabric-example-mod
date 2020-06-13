package net.funguscow.foodie.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public abstract class FoodieConfig<T> {

    public static final File CONFIG_PATH = new File(FabricLoader.getInstance().getConfigDirectory(), FoodieMod.MODID);

    protected static Properties properties = new Properties();

    private static FoodieConfig[] configs;

    protected static boolean created = false,
            registered = false,
            client_ran = false;
    protected List<T> listings;

    protected Map<Identifier, Block> blocks;
    protected Map<Identifier, Item> items;

    public static void Setup(){
        if(created)
            return;
        configs = new FoodieConfig[]{
                CropsConfig.Instance,
                StemConfig.Instance,
                TreeConfig.Instance,
                WoodConfig.Instance,
                FoodConfig.Instance
        };
        for(FoodieConfig config : configs)
            config.setup();
        created = true;
    }

    public static void Register(){
        if(registered)
            return;
        for(FoodieConfig config : configs)
            config.register();
        registered = true;
    }

    public static void RegisterClient(){
        if(client_ran)
            return;
        for(FoodieConfig config : configs)
            config.registerClient();
        client_ran = true;
    }

    public static String getProperty(String key, String def){
        return properties.getProperty(key, def);
    }

    protected FoodieConfig(String config_name, Class<T> type){
        listings = new ArrayList<>();
        blocks = new HashMap<>();
        items = new HashMap<>();
        File config_file = new File(CONFIG_PATH, config_name + ".json");
        try(FileReader reader = new FileReader(config_file)){
            JsonArray array = GsonHelper.getGson().fromJson(reader, JsonArray.class);
            for(JsonElement element : array){
                listings.add(GsonHelper.getGson().fromJson(element, type));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void cutout(Identifier id){
        BlockRenderLayerMap.INSTANCE.putBlock(blocks.get(id), RenderLayer.getCutout());
    }

    protected void blockColor(List<Integer> colors, Identifier blockID){
        ColorProviderRegistry.BLOCK.register((state, view, pos, tint) -> colors.get(tint), blocks.get(blockID));
    }

    protected void itemColor(List<Integer> colors, Identifier itemID){
        ColorProviderRegistry.ITEM.register((stack, tint) -> colors.get(tint), items.get(itemID));
    }

    public void register(){
        for(Map.Entry<Identifier, Block> pair : blocks.entrySet()){
            Registry.register(Registry.BLOCK, pair.getKey(), pair.getValue());
        }
        for(Map.Entry<Identifier, Item> pair : items.entrySet()){
            Registry.register(Registry.ITEM, pair.getKey(), pair.getValue());
        }
    }

    static{
        try(FileReader reader = new FileReader(new File(CONFIG_PATH, "config.properties"))){
            properties.load(reader);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public abstract void setup();
    public abstract void registerClient();

}
