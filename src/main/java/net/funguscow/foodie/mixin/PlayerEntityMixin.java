package net.funguscow.foodie.mixin;

import net.funguscow.foodie.config.FoodieConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins to enable hunger on peaceful difficulty
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    /**
     * Auto regen of health/hunger will never occur
     * @param world Passed by captured obj
     * @return NORMAL (doesn't matter as long as it's not peaceful)
     */
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    public Difficulty neverPeaceful(World world){
        return Boolean.parseBoolean(FoodieConfig.getProperty("peaceful_hunger", "true"))
                ? Difficulty.NORMAL
                : world.getDifficulty();
    }

    @Shadow
    public void addExhaustion(float exhaustion){}

    private static Float hunger = null;

    @Inject(method = "tickMovement", at = @At(value = "HEAD"))
    public void hunger(CallbackInfo ci){
        if(hunger == null)
            hunger = Float.parseFloat(FoodieConfig.getProperty("passiveHunger", "0"));
        addExhaustion(hunger);
    }
}
