package net.funguscow.foodie.config.pojo;

import java.util.List;

/**
 * POJO for crop types
 */
public class CropListing {

    public int maxAge = 7;
    public boolean cropIsSeed = false;
    public boolean canRightClick = false;
    public boolean findInWild = true;
    public int hunger = 1;
    public int value = 1;
    public float saturation = 1;
    public String seedName;
    public String plantName;
    public String produceName;
    public String english;
    public List<String> cropTexture;
    public List<String> seedLayers;
    public List<String> produceLayers;
    public List<String> media;

}
