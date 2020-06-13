package net.funguscow.foodie.main;

import com.google.gson.JsonObject;
import net.funguscow.foodie.config.pojo.TagListing;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TagWriter extends ComponentWriter<TagListing> {

    public TagWriter(ConfigConverter converter){
        super(converter);
    }

    protected String getKey(){
        return "tags";
    }

    protected Class<TagListing> getType(){
        return TagListing.class;
    }

    protected void write(TagListing tag){
        Identifier id = new Identifier(tag.id);
        tag.members.forEach(s -> converter.putItemTag(id, s));
        writeTags();
    }

    private void writeTags(){
        List<Identifier> delay = new ArrayList<>();
        for(Identifier tag : converter.blockTags.keySet()){
            if(tag.getNamespace().equals("minecraft")) {
                delay.add(tag);
                continue;
            }
            Path tagFile = Paths.get("data", tag.getNamespace(), "tags", "blocks", tag.getPath() + ".json");
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            obj.add("values", GsonHelper.getGson().toJsonTree(converter.blockTags.get(tag)));
            converter.writeJson(tagFile, obj);
        }
        for(Identifier tag : delay){
            Path tagFile = Paths.get("data", tag.getNamespace(), "tags", "blocks", tag.getPath() + ".json");
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            obj.add("values", GsonHelper.getGson().toJsonTree(converter.blockTags.get(tag)));
            converter.writeJson(tagFile, obj);
        }

        delay.clear();
        for(Identifier tag : converter.itemTags.keySet()){
            if(tag.getNamespace().equals("minecraft")) {
                delay.add(tag);
                continue;
            }
            Path tagFile = Paths.get("data", tag.getNamespace(), "tags", "items", tag.getPath() + ".json");
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            obj.add("values", GsonHelper.getGson().toJsonTree(converter.itemTags.get(tag)));
            converter.writeJson(tagFile, obj);
        }
        for(Identifier tag : delay){
            Path tagFile = Paths.get("data", tag.getNamespace(), "tags", "items", tag.getPath() + ".json");
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            obj.add("values", GsonHelper.getGson().toJsonTree(converter.itemTags.get(tag)));
            converter.writeJson(tagFile, obj);
        }
    }

}
