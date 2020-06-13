package net.funguscow.foodie.blocks.shipping;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.config.FoodieConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ShippingBinBlock extends Block implements BlockEntityProvider  {

    public static Map<Identifier, Integer> values;

    static{
        values = new HashMap<>();
        values.put(new Identifier("minecraft", "stone"), 50);
    }

    public static void putItem(Identifier id, int value){
        if(value > 0)
            values.put(id, value);
    }

    public ShippingBinBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view){
        return new ShippingBinEntity();
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if(world.isClient)
            return ActionResult.PASS;
        ItemStack handStack = player.getStackInHand(hand);
        ActionResult result = ActionResult.PASS;
        ShippingBinEntity entity = (ShippingBinEntity)world.getBlockEntity(pos);
        if(!handStack.isEmpty()){
            Item item = handStack.getItem();
            boolean send = false;
            Identifier id = Registry.ITEM.getId(item);
            if(values.containsKey(id)){
                send = true;
            }
            else if(TagRegistry.item(ShippingBinEntity.VANILLA_VALUE).contains(item)){
                send = true;
            }
            else if((TagRegistry.item(ShippingBinEntity.VANILLA_SEEDS).contains(item)
                    || TagRegistry.item(ShippingBinEntity.SEEDS).contains(item))) {
                send = Integer.parseInt(FoodieConfig.getProperty("seed_value", "1")) > 0;
            }
            if(send){
                entity.setInvStack(0, new ItemStack(item, 1));
                if(!player.abilities.creativeMode)
                    handStack.decrement(1);
                result = ActionResult.CONSUME;
            }
        }
        ItemStack output = entity.getInvStack(1);
        if(!output.isEmpty()){
            player.inventory.offerOrDrop(world, output.copy());
            entity.removeInvStack(1);
            if(result == ActionResult.PASS)
                result = ActionResult.SUCCESS;
        }
        player.addChatMessage(new LiteralText("Now at " + entity.fillLevel + "/" + ShippingBinEntity.MAX_FULL), false);
        return result;
    }

}
