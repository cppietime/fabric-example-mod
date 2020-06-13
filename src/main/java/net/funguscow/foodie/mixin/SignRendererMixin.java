package net.funguscow.foodie.mixin;

import net.funguscow.foodie.injection.SignTypeInject;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignBlockEntityRenderer.class)
public abstract class SignRendererMixin {

    @Redirect(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    public void coloredRender(ModelPart subject,
                              MatrixStack stack,
                              VertexConsumer consumer,
                              int i,
                              int j,
                              SignBlockEntity entity,
                              float f,
                              MatrixStack argStack,
                              VertexConsumerProvider provider,
                              int ai,
                              int aj){
        SignType type = SignType.OAK;
        Block block = entity.getCachedState().getBlock();
        if(block instanceof AbstractSignBlock)
            type = ((AbstractSignBlock)block).getSignType();
        Integer color = SignTypeInject.getColor(type);
        if(color != null){
            String key = type.getName();
            float r = ((color >> 16) & 0xff) / 255f,
                g = ((color >> 8) & 0xff) / 255f,
                b = (color & 0xff) / 255f;
            subject.render(stack, consumer, i, j, r, g, b, 1);
        }else{
            subject.render(stack, consumer, i, j);
        }
    }

}
