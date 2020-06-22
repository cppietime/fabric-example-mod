package net.funguscow.foodie.config.pojo;

import java.util.List;

public class TreeListing {

    public int density = 1, extraDensity = 1, height = 4;
    public float extraChance = 0.5f;
    public String logTag = "minecraft:oak_logs";
    public float baseOdds = 0.005f;
    public int value = 10;
    public String trunkName, leafName, treeName, fruitName;
    public String treeEnglish, fruitEnglish;
    public List<String> biomes;
    public int hunger;
    public float saturation;
    public List<String> woodTexture, barkTexture, leafTexture;
    public List<String> saplingTexture, fruitTexture;

}
