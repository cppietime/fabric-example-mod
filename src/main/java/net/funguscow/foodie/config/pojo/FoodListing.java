package net.funguscow.foodie.config.pojo;

import java.util.List;

public class FoodListing {

    public int value = 1;
    public int count = 1;
    public float xp = 0.35f;
    public boolean glint = false;
    public String name, english;
    public int hunger;
    public float saturation;
    public List<String> textures;
    public List<String> ingredients;
    public List<String> tags;
    public String craft;
    public List<Effect> effects;

    public static class Effect{
        public float chance = 1f;
        public int duration = 200, level = 1;
        public String name;
    }

}
