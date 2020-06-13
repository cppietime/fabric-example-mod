package net.funguscow.foodie.main;

import com.google.gson.JsonObject;
import net.funguscow.foodie.utils.GsonHelper;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class LangWriter {

    private final ConfigConverter converter;

    public LangWriter(ConfigConverter converter){
        this.converter = converter;
    }

    public void writeLang(){
        JsonObject object;
        try(FileReader reader = new FileReader(new File(converter.path, "lang" + File.separator + "en_us.json"))){
            object = GsonHelper.getGson().fromJson(reader, JsonObject.class);
        }catch(Exception e){
            object = new JsonObject();
        }
        for(Map.Entry<String, String> entry : converter.enUs.entrySet()){
            if(object.get(entry.getKey()) != null)
                object.remove(entry.getKey());
            object.addProperty(entry.getKey(), entry.getValue());
        }
        converter.writeJson(converter.langFile, object);
    }

}
