package net.funguscow.foodie.mixin;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityMixin {

    @Inject(method = "supports(Lnet/minecraft/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    private void supports(Block block, CallbackInfoReturnable<Boolean> info){
        if(equals(BlockEntityType.SIGN) && block instanceof AbstractSignBlock)
            info.setReturnValue(true);
    }

}
