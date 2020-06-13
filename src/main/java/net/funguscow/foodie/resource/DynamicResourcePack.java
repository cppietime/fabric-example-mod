package net.funguscow.foodie.resource;

import com.google.gson.JsonObject;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.main.ConfigConverter;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class DynamicResourcePack implements ResourcePack {

    private final Map<ResourceType,
            Map<String, Map<String, String>>> jsonResources;

    public static DynamicResourcePack Instance = new DynamicResourcePack();

    public DynamicResourcePack(){
        jsonResources = new HashMap<>();
        jsonResources.put(ResourceType.CLIENT_RESOURCES, new LinkedHashMap<>());
        jsonResources.put(ResourceType.SERVER_DATA, new LinkedHashMap<>());
    }

    public void register(ResourceType type, Identifier id, String str){
        String namespace = id.getNamespace(), path = id.getPath();
        Map<String, String> current = jsonResources.get(type).get(namespace);
        if(current == null){
            current = new LinkedHashMap<>();
            jsonResources.get(type).put(namespace, current);
        }
        current.put(path, str);
    }

    public void register(ResourceType type, Path id, String str){
        register(type, new Identifier(id.getName(0).toString(),
                id.subpath(1, id.getNameCount()).toString().replace("\\", "/")), str);
    }

    @Override
    public InputStream openRoot(String fileName) throws IOException {
        throw new IOException("Hehe can't do that little boy");
    }

    @Override
    public InputStream open(ResourceType type, Identifier id)  {
        String namespace = id.getNamespace(), path = id.getPath();
        String value = jsonResources.get(type).get(namespace).get(path);
        return new ByteArrayInputStream(value.getBytes());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        Collection<Identifier> ret = new ArrayList<>();
        Map<String, String> resourceMap = jsonResources.get(type).get(namespace);
        if(resourceMap != null) {
            for (String path : resourceMap.keySet()){
                if(path.startsWith(prefix) && pathFilter.test(path))
                    ret.add(new Identifier(namespace, path));
            }
        }
        return ret;
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        String namespace = id.getNamespace(), path = id.getPath();
        if(!jsonResources.get(type).containsKey(namespace))
            return false;
        return jsonResources.get(type).get(namespace).containsKey(path);
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return new HashSet<>(jsonResources.get(type).keySet());
//        Set<String> namespaces = new HashSet<>();
//        for(Map<Identifier, String> submap : jsonResources.get(type).values()){
//            submap.keySet().forEach(i -> namespaces.add(i.getNamespace()));
//        }
//        return namespaces;
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return metaReader.fromJson(new JsonObject());
    }

    @Override
    public String getName() {
        return "dynapack";
    }

    @Override
    public void close() {

    }
}
