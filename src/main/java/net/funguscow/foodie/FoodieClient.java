package net.funguscow.foodie;

import net.fabricmc.api.ClientModInitializer;
import net.funguscow.foodie.config.FoodieConfig;
import net.funguscow.foodie.main.ConfigConverter;

public class FoodieClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigConverter.register(FoodieMod.MODID);

        FoodieConfig.Setup();
        FoodieConfig.RegisterClient();
    }
}
