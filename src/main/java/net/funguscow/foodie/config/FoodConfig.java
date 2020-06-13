package net.funguscow.foodie.config;

import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.blocks.shipping.ShippingBinBlock;
import net.funguscow.foodie.config.pojo.FoodListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.List;

public class FoodConfig extends FoodieConfig<FoodListing> {

    public static final FoodConfig Instance = new FoodConfig();

    public FoodConfig(){
        super("foods", FoodListing.class);
    }

    private static StatusEffect effectByName(String name){
        try {
            return (StatusEffect)StatusEffects.class.getDeclaredField(name.toUpperCase()).get(null);
        }catch(Exception e){
            return null;
        }
    }

    private static void addEffect(FoodComponent.Builder builder, FoodListing.Effect effect){
        StatusEffect status = effectByName(effect.name);
        if(status != null){
            builder.statusEffect(new StatusEffectInstance(status, effect.duration, effect.level), effect.chance);
        }
    }

    @Override
    public void setup() {
        for(FoodListing food : listings){
            Item.Settings settings = new Item.Settings().group(FoodieMod.MAIN_GROUP);
            Item foodItem;
            if(food.hunger + food.saturation > 0 || (food.effects != null)) {
                FoodComponent.Builder builder = new FoodComponent.Builder()
                        .hunger((int) (food.hunger / Float.parseFloat(FoodieConfig.getProperty("hunger_multiplier", "1"))))
                        .saturationModifier(food.saturation);
                if (food.effects != null) {
                    food.effects.forEach(e -> addEffect(builder, e));
                    builder.alwaysEdible();
                }
                settings = settings.food(builder.build());
            }
            if(food.effects != null || food.glint)
                foodItem = new EnchantedGoldenAppleItem(settings);
            else
                foodItem = new Item(settings);
            items.put(new Identifier(FoodieMod.MODID, food.name), foodItem);
        }
    }

    @Override
    public void register() {
        super.register();
        for(FoodListing food : listings){
            if(food.value > 0)
                ShippingBinBlock.putItem(new Identifier(FoodieMod.MODID, food.name), food.value);
        }
    }

    @Override
    public void registerClient() {
        for(FoodListing food : listings){
            final List<Integer> colors = GsonHelper.colors(food.textures);
            itemColor(colors, new Identifier(FoodieMod.MODID, food.name));
        }
    }
}
