package net.funguscow.foodie.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.funguscow.foodie.utils.GsonHelper;

import java.io.File;
import java.io.FileReader;

public abstract class ComponentWriter<T> {

    protected ConfigConverter converter;

    protected ComponentWriter(ConfigConverter converter){
        this.converter = converter;
    }

    public void convert(){
        try (FileReader reader = new FileReader(new File(converter.path, getKey() + ".json"))) {
            JsonArray array = GsonHelper.getGson().fromJson(reader, JsonArray.class);
            for (JsonElement json : array) {
                T obj = GsonHelper.getGson().fromJson(json, getType());
                write(obj);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected abstract Class<T> getType();

    protected abstract String getKey();

    protected abstract void write(T obj);

}
