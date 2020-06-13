package net.funguscow.foodie.blocks;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.*;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.Set;

/**
 * Custom gourd block
 */
public class FoodieGourdBlock extends GourdBlock {

    /**
     * If not otherwise specified, fruits can appear on these blocks
     */
    private final static Set<Block> defaultSupports = ImmutableSet.of(
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.FARMLAND,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL
    );

    private StemBlock stemBlock;
    private AttachedStemBlock attachedStemBlock;
    private Item seeds;
    private final int stages;
    private Collection<Block> media, supports;

    public FoodieGourdBlock(Settings settings, int stages){
        super(settings);
        this.stages = Math.min(7, stages);
    }

    /**
     *
     * @return Blocks on which this can be planted
     */
    public Collection<Block> getMedia(){
        return media;
    }

    public FoodieGourdBlock withMedia(Collection<Block> media){
        this.media = media;
        return this;
    }
    
    public Set<Block> getSupports() {
        if(supports == null)
            return defaultSupports;
        return new ObjectArraySet<>(supports);
    }

    public FoodieGourdBlock withSupports(Collection<Block> supports){
        this.supports = supports;
        return this;
    }

    public FoodieGourdBlock withSeeds(Item seeds){
        this.seeds = seeds;
        return this;
    }

    public int getStages(){
        return stages;
    }

    public FoodieGourdBlock withStems(StemBlock stemBlock, AttachedStemBlock attachedStemBlock){
        this.stemBlock = stemBlock;
        this.attachedStemBlock = attachedStemBlock;
        return this;
    }

    public Item getSeeds(){
        return seeds;
    }

    public StemBlock getStem(){
        return stemBlock;
    }

    public AttachedStemBlock getAttachedStem(){
        return attachedStemBlock;
    }

}
