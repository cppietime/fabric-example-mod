package net.funguscow.foodie.config.pojo;

import java.util.List;

public class FoodListing {

    public int value = 10;
    public int count = 1;
    public float xp = 0.35f;
    public boolean glint = false;
    public int hunger = 1;
    public float saturation = 1;
    public String name, english;
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
