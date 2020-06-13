package net.funguscow.foodie.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ImplementedInventory extends Inventory {

    DefaultedList<ItemStack> getItems();

    static ImplementedInventory of(DefaultedList<ItemStack> items){
        return () -> items;
    }

    default int getInvSize(){
        return getItems().size();
    }

    default ItemStack getInvStack(int slot){
        return getItems().get(slot);
    }

    default boolean isInvEmpty(){
        for(ItemStack stack : getItems()) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    default ItemStack takeInvStack(int slot, int count){
        ItemStack stack = Inventories.splitStack(getItems(), slot, count);
        if(!stack.isEmpty())
            markDirty();
        return stack;
    }

    default ItemStack removeInvStack(int slot){
        return Inventories.removeStack(getItems(), slot);
    }

    default void setInvStack(int slot, ItemStack stack){
        getItems().set(slot, stack);
        if(stack.getCount() > getInvMaxStackAmount())
            stack.setCount(getInvMaxStackAmount());
    }

    default void clear(){
        getItems().clear();
    }

    default void markDirty(){}

    default boolean canPlayerUseInv(PlayerEntity player){return false;}

}
