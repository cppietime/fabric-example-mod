package net.funguscow.foodie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.funguscow.foodie.blocks.shipping.ShippingBinBlock;
import net.funguscow.foodie.blocks.shipping.ShippingBinEntity;
import net.funguscow.foodie.config.FoodieConfig;
import net.funguscow.foodie.injection.WoodStrippings;
import net.funguscow.foodie.main.ConfigConverter;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class FoodieMod implements ModInitializer {

	public static final String MODID = "foodie";
	public static final String[] MOLDS = {"fish", "pork", "beef", "chicken", "egg", "mutton"};

	public static ShippingBinBlock SHIPPING_BIN;
	public static Item SHIPPING_BIN_ITEM;
	public static ItemGroup MAIN_GROUP;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ConfigConverter.register(MODID);

		MAIN_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "maingroup"),
				() -> new ItemStack(SHIPPING_BIN));

		FoodieConfig.Setup();
		FoodieConfig.Register();

		WoodStrippings.register();

		SHIPPING_BIN = new ShippingBinBlock(FabricBlockSettings.copy(Blocks.OAK_LOG));
		SHIPPING_BIN_ITEM = new BlockItem(SHIPPING_BIN, new Item.Settings().group(MAIN_GROUP));

		ShippingBinEntity.registerType(SHIPPING_BIN);

		Registry.register(Registry.BLOCK, new Identifier(MODID, "shipping_bin"), SHIPPING_BIN);
		Registry.register(Registry.ITEM, new Identifier(MODID, "shipping_bin"), SHIPPING_BIN_ITEM);

		for(String mold : MOLDS){
			Item moldItem = new Item(new Item.Settings().group(MAIN_GROUP));
			try {
				Field remainder = Item.class.getDeclaredField("recipeRemainder");
				remainder.setAccessible(true);
				remainder.set(moldItem, moldItem);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Registry.register(Registry.ITEM, new Identifier(MODID, mold + "_mold"), moldItem);
		}

	}
}
