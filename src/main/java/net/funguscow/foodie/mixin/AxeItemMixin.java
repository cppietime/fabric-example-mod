package net.funguscow.foodie.mixin;

import net.funguscow.foodie.injection.WoodStrippings;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

/**
 * Redirect call to registered stripped blocks to add in provided blocks
 */
@Mixin(AxeItem.class)
public abstract class AxeItemMixin {

    @Redirect(method="useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "FIELD",
                target = "Lnet/minecraft/item/AxeItem;STRIPPED_BLOCKS:Ljava/util/Map;",
                opcode = Opcodes.GETSTATIC))
    private Map<Block, Block> redirectedStripping(){
        return WoodStrippings.STRIPPINGS;
    }

}
