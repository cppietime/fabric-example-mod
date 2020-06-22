package net.funguscow.foodie.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.funguscow.foodie.config.pojo.CropListing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class FoodieCropBlock extends CropBlock {

    private final CropListing specs;
    private Item seedItem;
    private Collection<Block> media;

    public FoodieCropBlock(Settings settings, CropListing specs) {
        super(settings);
        this.specs = specs;
    }

    public FoodieCropBlock withSeedsItem(Item seedItem){
        this.seedItem = seedItem;
        return this;
    }

    public FoodieCropBlock withMedia(Collection<Block> media){
        this.media = media;
        return this;
    }

    @Override
    public int getMaxAge(){
        return specs.maxAge;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemConvertible getSeedsItem(){
        return seedItem;
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos){
        if(media == null)
            return super.canPlantOnTop(floor, view, pos);
        return media.contains(floor.getBlock());
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if(!world.isClient && isMature(state) && specs.canRightClick) {
            DefaultedList<ItemStack> drops = DefaultedList.copyOf(null,
                    getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getStackInHand(hand))
                            .toArray(new ItemStack[0]));
            for (int i = 0; i < drops.size(); i++) {
                ItemStack stack = drops.get(i);
                if (stack.getItem() == getSeedsItem() && !specs.cropIsSeed) {
                    ItemStack repl = stack.copy();
                    repl.decrement(1);
                    drops.set(i, repl);
                }
            }
            world.setBlockState(pos, state.with(AGE, 0));
            ItemScatterer.spawn(world, pos, drops);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public static float getAvailableMoisture(Block block, BlockView view, BlockPos pos){
        return CropBlock.getAvailableMoisture(block, view, pos);
    }

}
