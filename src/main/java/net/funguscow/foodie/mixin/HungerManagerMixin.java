package net.funguscow.foodie.mixin;

import net.funguscow.foodie.config.FoodieConfig;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Allow hunger to decrease on peaceful
 */
@Mixin(HungerManager.class)
public class HungerManagerMixin {

	/**
	 * Allows hunger to be taken on peaceful by never allowing this peaceful check to succeed
	 * @param world World object to act upon
	 * @return Not peaceful (NORMAL)
	 */
	@Redirect(method = "update", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	public Difficulty neverPeaceful(World world){
		return Boolean.parseBoolean(FoodieConfig.getProperty("peaceful_hunger", "true"))
				? Difficulty.NORMAL
				: world.getDifficulty();
	}

	private static Float hungerMul = null;

	@Redirect(method = "eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/item/FoodComponent;getHunger()I"))
	public int hungerInject(FoodComponent component){
		if(hungerMul == null)
			hungerMul = Float.parseFloat(FoodieConfig.getProperty("hunger_multiplier", "1"));
		int base = component.getHunger();
		if(base == 0)
			return 0;
		return (int)Math.max(1, base * hungerMul);
	}

}
