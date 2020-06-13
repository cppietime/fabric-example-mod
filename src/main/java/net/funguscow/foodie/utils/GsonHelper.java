package net.funguscow.foodie.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class GsonHelper {

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static List<String> firstTextures(List<String> textures){
        return textures.stream().map(t -> t.split(",")[0]).collect(Collectors.toList());
    }

    public static List<Integer> colors(List<String> textures){
        return textures.stream().map(t -> Integer.parseInt(t.split(",")[1], 16)).collect(Collectors.toList());
    }

}
