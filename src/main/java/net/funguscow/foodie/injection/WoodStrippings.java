package net.funguscow.foodie.injection;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflection-based hack to add custom wood types to be stripped by axes
 */
public class WoodStrippings {

    public static Map<Block, Block> STRIPPINGS = new HashMap<>();

    public static void addStrip(Block base, Block stripped){
        STRIPPINGS.put(base, stripped);
    }

    public static void register(){
        try{
            Field field = AxeItem.class.getDeclaredField("STRIPPED_BLOCKS");
            field.setAccessible(true);
            Map<Block, Block> map = (Map<Block, Block>)field.get(null);
            STRIPPINGS.putAll(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
