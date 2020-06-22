package net.funguscow.foodie.config.pojo;

import java.util.List;

public class StemListing {

    public int maxAge = 7, value = 10, count = 1, tries = 64;
    public String stemTexture = "minecraft:block/pumpkin_stem";
    public String attachedTexture = "minecraft:block/attached_pumpkin_stem";
    public boolean seedFromGourd = true;
    public String stemName, seedName, gourdName;
    public String english;
    public List<String> seedLayers, gourdTexture;
    public List<String> media, supports;
    public List<String> biomes;

}
