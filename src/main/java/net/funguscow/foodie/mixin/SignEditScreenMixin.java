package net.funguscow.foodie.mixin;

import net.funguscow.foodie.injection.SignTypeInject;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin {

    @Shadow
    private SignBlockEntity sign;

    @Redirect(method = "render(IIF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    public void coloredRender(ModelPart subject,
                              MatrixStack stack,
                              VertexConsumer consumer,
                              int i,
                              int j){
        SignType type = SignType.OAK;
        Block block = sign.getCachedState().getBlock();
        if(block instanceof AbstractSignBlock)
            type = ((AbstractSignBlock)block).getSignType();
        Integer color = SignTypeInject.getColor(type);
        if(color != null){
            float r = ((color >> 16) & 0xff) / 255f,
                    g = ((color >> 8) & 0xff) / 255f,
                    b = (color & 0xff) / 255f;
            subject.render(stack, consumer, i, j, r, g, b, 1);
        }else{
            subject.render(stack, consumer, i, j);
        }
    }

}
