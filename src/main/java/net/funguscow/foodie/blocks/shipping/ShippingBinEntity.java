package net.funguscow.foodie.blocks.shipping;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.blocks.ImplementedInventory;
import net.funguscow.foodie.config.FoodieConfig;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class ShippingBinEntity extends BlockEntity implements ImplementedInventory, SidedInventory {

    public static BlockEntityType<ShippingBinEntity> SHIPPING_BIN_ENTITY;
    public static Integer MAX_FULL = null;
    public static final Identifier VANILLA_SEEDS = new Identifier(FoodieMod.MODID, "vanilla_seeds"),
            SEEDS = new Identifier(FoodieMod.MODID, "seeds"),
            VANILLA_VALUE = new Identifier(FoodieMod.MODID, "vanilla_value");

    private final int vanillaValue, seedValue;
    private final DefaultedList<ItemStack> items;
    int fillLevel;

    public static void registerType(Block block){
        SHIPPING_BIN_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, FoodieMod.MODID + ":" + "shipping_entity", BlockEntityType.Builder.create(ShippingBinEntity::new, block).build(null));
    }

    public ShippingBinEntity(){
        super(SHIPPING_BIN_ENTITY);
        fillLevel = 0;
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
        vanillaValue = Integer.parseInt(FoodieConfig.getProperty("vanilla_value", "1"));
        seedValue = Integer.parseInt(FoodieConfig.getProperty("seed_value", "1"));
    }

    public void maybeConvert(){
        if(MAX_FULL == null)
            MAX_FULL = Integer.parseInt(FoodieConfig.getProperty("fill_level", "100"));
        if(fillLevel >= MAX_FULL){
            int count = fillLevel / MAX_FULL;
            int newCount = Math.min(getInvMaxStackAmount(), items.get(1).getCount() + count);
            fillLevel -= (newCount - items.get(1).getCount()) * MAX_FULL;
            items.set(1, new ItemStack(Items.EMERALD, newCount));
        }
    }

    public void setInvStack(int slot, ItemStack stack){
        Item item = stack.getItem();
        if(ShippingBinBlock.values.containsKey(Registry.ITEM.getId(item)))
            fillLevel += ShippingBinBlock.values.get(Registry.ITEM.getId(item));
        else if(TagRegistry.item(VANILLA_VALUE).contains(item))
            fillLevel += vanillaValue;
        else if(TagRegistry.item(VANILLA_SEEDS).contains(item)
                || TagRegistry.item(SEEDS).contains(item))
            fillLevel += seedValue;
        maybeConvert();
    }

    public ItemStack removeInvStack(int slot){
        ItemStack ret = ImplementedInventory.super.removeInvStack(slot);
        maybeConvert();
        return ret;
    }

    @Override
    public DefaultedList<ItemStack> getItems(){
        return items;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag){
        Inventories.toTag(tag, items);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag){
        super.fromTag(tag);
        Inventories.fromTag(tag, items);
    }

    @Override
    public int[] getInvAvailableSlots(Direction side) {
        if(side == Direction.DOWN)
            return new int[]{1};
        return new int[]{0};
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        Item item = stack.getItem();
        return dir != Direction.DOWN && slot == 0
                && (ShippingBinBlock.values.containsKey(Registry.ITEM.getId(item))
                    || TagRegistry.item(VANILLA_VALUE).contains(item)
                    || TagRegistry.item(SEEDS).contains(item) && seedValue > 0
                    || TagRegistry.item(VANILLA_SEEDS).contains(item) && seedValue > 0);
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 1;
    }
}
