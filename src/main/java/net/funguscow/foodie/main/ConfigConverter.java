package net.funguscow.foodie.main;

import net.fabricmc.loader.api.FabricLoader;
import net.funguscow.foodie.FoodieMod;
import net.funguscow.foodie.config.pojo.CropListing;
import net.funguscow.foodie.config.pojo.StemListing;
import net.funguscow.foodie.resource.DynamicResourcePack;
import net.funguscow.foodie.utils.GsonHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//TODO next handle non-extant config file

public class ConfigConverter {

    public static String modid = FoodieMod.MODID;

    public final Path itemModels, blockModels, blockStates, lootTables, recipes, langFile;
    public File path, templates;

    public Map<Identifier, List<String>> blockTags, itemTags;
    public Map<String, String> enUs;

    public static class ItemModel{
        public final String parent = "item/generated";
        public Map<String, String> textures;
        public transient int i;
        public ItemModel(List<String> layers){
            i = 0;
            textures = layers.stream().collect(Collectors.toMap((layer) -> "layer" + (i++), layer -> layer));
        }
    }

    public static class ItemBlockModel{
        public String parent;
        public ItemBlockModel(String block){
            parent = block;
        }
    }

    public static class CubeBlockModel{
        public String parent;
        public Map<String, String> textures;
        public CubeBlockModel(String id, String keys, List<String>... textures){
            String prefix = textures[0].size() == 1 ? "tinted_" : "dual_";
            if(id != null)
                this.parent = id.replace(":", ":block/templates/" + prefix);
            this.textures = new HashMap<>();
            String[] keysets = keys.split(";");
            for(int i = 0; i < textures.length; i++){
                String[] keyset = keysets[i].split(",");
                List<String> texture = textures[i];
                for(int j = 0; j < texture.size(); j++){
                    String value = texture.get(j).split(",")[0];
                    this.textures.put(keyset[j], value);
                }
            }
        }
    }

    public static class CropBlockModel {
        public String parent;
        public Map<String, String> textures;
        public CropBlockModel(String key, String crop){
            parent = "foodie:block/templates/tinted_" + key;
            textures = Collections.singletonMap(key, crop);
        }
        public CropBlockModel(String key, String base, String overlay){
            parent = "foodie:block/templates/dual_" + key;
            textures = new HashMap<>();
            textures.put(key, base);
            textures.put("overlay", overlay);
        }
    }

    public static class StemBlockModel{
        public final String parent;
        public Map<String, String> textures;
        public StemBlockModel(int stage, StemListing stem, boolean isAttached){
            parent = isAttached ? "minecraft:block/stem_fruit" : "minecraft:block/stem_growth" + stage;
            textures = new HashMap<>();
            textures.put("stem", stem.stemTexture.split(",")[0]);
            if(isAttached)
                textures.put("upperstem", stem.attachedTexture.split(",")[0]);
        }
    }

    public static class MonoBlockState{
        public Map<String, Map<String, String>> variants;
        public MonoBlockState(String model){
            variants = Collections.singletonMap("", Collections.singletonMap("model", model));
        }
    }

    public static class GrowingBlockState {
        public Map<String, Map<String, String>> variants;
        public GrowingBlockState(String prefix, int len){
            variants = new HashMap<>();
            for(int i = 0; i <= len; i++){
                String suffix = "_stage" + i;
                String key = String.format("age=%d", i);
                variants.put(key, Collections.singletonMap("model", prefix+suffix));
            }
        }
    }

    public static class DirBlockState {
        public Map<String, Direction> variants;
        public static class Direction{
            public String model;
            public int y;
            public int x;
            public Direction(String model, int x, int y){
                this.model = model;
                this.x = x;
                this.y = y;
            }
        }
        public static DirBlockState fourWay(String model){
            DirBlockState ret = new DirBlockState();
            ret.variants = new HashMap<>();
            ret.variants.put("facing=west", new Direction(model, 0, 0));
            ret.variants.put("facing=north", new Direction(model, 0, 90));
            ret.variants.put("facing=east", new Direction(model, 0, 180));
            ret.variants.put("facing=south", new Direction(model, 0, 270));
            return ret;
        }
        public static DirBlockState axes(String model){
            DirBlockState ret = new DirBlockState();
            ret.variants = new HashMap<>();
            ret.variants.put("axis=y", new Direction(model, 0, 0));
            ret.variants.put("axis=z", new Direction(model, 90, 0));
            ret.variants.put("axis=x", new Direction(model, 90, 90));
            return ret;
        }
    }

    public static class LootTable {
        public String type = "minecraft:block";
        public List<LootPool> pools;
        public List<Function> functions;

        public static class LootPool {
            public final float rolls = 1.0f;
            public List<Entry> entries;
            public List<Condition> conditions;

            public static class Entry {
                public String type;
                public List<Entry> children;
                public List<Function> functions;
                public List<Condition> conditions;
                public String name;
            }

            public static class Condition {
                public String condition;
                public String block;
                public Map<String, String> properties;
                public static Condition onMature(CropListing crop){
                    Condition ret = new Condition();
                    ret.condition = "minecraft:block_state_property";
                    ret.block = modid + ":" + crop.plantName;
                    ret.properties = Collections.singletonMap("age", crop.maxAge + "");
                    return ret;
                }
            }
        }

        public static class Function {
            public String function;
            public String enchantment;
            public String formula;
            public Parameters parameters;

            public static Function fortuneBonus(){
                Function ret = new Function();
                ret.function = "minecraft:apply_bonus";
                ret.enchantment = "minecraft:fortune";
                ret.formula = "minecraft:binomial_with_bonus_count";
                ret.parameters = new Parameters();
                return ret;
            }

            public static class Parameters {
                public final int extra = 3;
                public final float probability = .5714f;
            }
        }

        public LootTable(CropListing crop){
            LootPool mainPool = new LootPool();
            LootPool.Entry anytime = new LootPool.Entry();
            if(crop.cropIsSeed) {
                anytime.type = "minecraft:item";
                anytime.name = crop.seedName;
            }
            else {
                LootPool.Entry yield = new LootPool.Entry();
                yield.type = "minecraft:item";
                yield.name = crop.produceName;
                LootPool.Condition mature = LootPool.Condition.onMature(crop);
                yield.conditions = Collections.singletonList(mature);
                LootPool.Entry seeds = new LootPool.Entry();
                seeds.type = "minecraft:item";
                seeds.name = crop.seedName;
                anytime.type = "minecraft:alternatives";
                anytime.children = Arrays.asList(yield, seeds);
            }
            mainPool.entries = Collections.singletonList(anytime);

            LootPool digPool = new LootPool();
            digPool.conditions = Collections.singletonList(LootPool.Condition.onMature(crop));
            LootPool.Entry uproot = new LootPool.Entry();
            uproot.type = "minecraft:item";
            uproot.name = crop.seedName;
            uproot.functions = Collections.singletonList(Function.fortuneBonus());
            digPool.entries = Collections.singletonList(uproot);

            pools = Arrays.asList(mainPool, digPool);
        }

        public LootTable(String name){
            LootPool pool = new LootPool();
            LootPool.Entry entry = new LootPool.Entry();
            entry.type = "minecraft:item";
            entry.name = name;
            pool.entries = Collections.singletonList(entry);
            pools = Collections.singletonList(pool);
        }
    }

    public static class ShapelessRecipe{
        public final String type = "minecraft:crafting_shapeless";
        public List<Map<String, String>> ingredients;
        public Map<String, Object> result;
        public ShapelessRecipe(int count, String resItem, String... ingredients){
            this(count, resItem, Arrays.asList(ingredients));
        }
        public ShapelessRecipe(int count, String resItem, List<String> ingredients){
            this.ingredients = new ArrayList<>();
            for(String ingredient : ingredients){
                String[] split = ingredient.split(":", 2);
                this.ingredients.add(Collections.singletonMap(split[0], split[1]));
            }
            result = new HashMap<>();
            result.put("item", resItem);
            if(count != 1)
                result.put("count", count);
        }
    }

    public static class CookingRecipe{
        public String type;
        public Map<String, String> ingredient;
        public String result;
        public float experience;
        public int cookingtime;
        public CookingRecipe(String type, String into, float xp, List<String> ingredients, int time){
            this.type = type;
            result = into;
            experience = xp;
            ingredient = ingredients.stream().collect(Collectors.toMap(i -> i.split(":")[0], i -> i.split(":", 2)[1]));
            cookingtime = time;
        }
    }

    private ConfigConverter(String modId, File source){
//        try{
//            copyConfigs();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        path = new File(source, modId);
        Path assets = Paths.get("assets", modId);//new File(destPath + "/assets/" + modId);
        Path langFolder = assets.resolve("lang");//new File(assets, "lang");
        Path data = Paths.get("data", modId);
        itemModels = assets.resolve("models").resolve("item");//new File(assets, "models/item");
        blockModels = assets.resolve("models").resolve("block");//new File(assets, "models/block");
        blockStates = assets.resolve("blockstates");//new File(assets, "blockstates");
        lootTables = data.resolve("loot_tables").resolve("blocks");//new File(data, "loot_tables/blocks");
        recipes = data.resolve("recipes");//new File(data, "recipes");
        langFile = langFolder.resolve("en_us.json");//new File(langFolder, "en_us.json");
        templates = new File(path, "templates");
        blockTags = new HashMap<>();
        itemTags = new HashMap<>();
        enUs = new HashMap<>();
    }

//    private void copyConfigs() throws Exception {
//        File base = new File(FabricLoader.getInstance().getConfigDirectory(), modid);
//        if(!base.isDirectory() && !base.mkdirs())
//            throw new RuntimeException("Config DIR does not exist and could not be created!");
//        File jarMaybe = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
//        if(jarMaybe.isFile()){
//            JarFile jarFile = new JarFile(jarMaybe);
//            Enumeration<JarEntry> entries = jarFile.entries();
//            while(entries.hasMoreElements()){
//                JarEntry entry = entries.nextElement();
//                String name = entry.getName();
//                if(name.startsWith("config/") && name.contains(".")){
//                    InputStream is = jarFile.getInputStream(entry);
//                    Path relative = Paths.get(name.substring("config/".length()));
//                    copyConfig(is, relative);
//                }
//            }
//        }else{
//            URL url = Launcher.class.getResource("/config");
//            File folder = new File(url.toURI());
//            final Path config = folder.toPath();
//            Files.walk(config).filter(Files::isRegularFile).forEach(p -> {
//                try {
//                    File f = p.toFile();
//                    Path relative = config.relativize(p);
//                    InputStream is = new FileInputStream(f);
//                    copyConfig(is, relative);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            });
//        }
//    }

    private void copyConfig(InputStream stream, Path name) throws IOException{
        File configBase = new File(FabricLoader.getInstance().getConfigDirectory(), modid);
        File dest = new File(configBase, name.toString());
        File folder = dest.getParentFile();
        if(!folder.isDirectory() && !folder.mkdirs())
            throw new RuntimeException("Cannot create config subfolder " + folder.getPath());
        if(!dest.isFile()){
            System.out.println("Copying to " + name.toString());
            Files.copy(stream, dest.toPath());
        }else
            System.out.println(name.toString() + " already exists!");
    }

    public void putBlockTag(Identifier tag, String block){
        List<String> tagList;
        if(blockTags.containsKey(tag))
            tagList = blockTags.get(tag);
        else {
            tagList = new ArrayList<>();
            blockTags.put(tag, tagList);
        }
        tagList.add(block);
    }

    public void putItemTag(Identifier tag, String item){
        List<String> tagList;
        if(itemTags.containsKey(tag))
            tagList = itemTags.get(tag);
        else {
            tagList = new ArrayList<>();
            itemTags.put(tag, tagList);
        }
        tagList.add(item);
    }

    public void putBothTags(Identifier tag, String item){
        putBlockTag(tag, item);
        putItemTag(tag, item);
    }

    public void translateItem(Identifier id, String text){
        enUs.put("item." + id.getNamespace() + "." + id.getPath(), text);
    }

    public void translateBlock(Identifier id, String text){
        enUs.put("block." + id.getNamespace() + "." + id.getPath(), text);
    }

    public void generalBlockModels(String name, Object state, Object blockModel, Object itemModel){
        writeJson(blockStates.resolve(name), state);
        writeJson(blockModels.resolve(name), blockModel);
        if(itemModel != null)
            writeJson(itemModels.resolve(name), itemModel);
    }

    public void selfLootTable(String name){
        LootTable table = new LootTable(name);
        writeJson(lootTables.resolve(name.split(":")[1] + ".json"), table);
    }

    public void templateFile(File source, Path dest, Object... params){
        try{
            String contents = new String(Files.readAllBytes(source.toPath()));
            writeLiteral(dest, String.format(contents, params));
//            Files.write(dest.toPath(), String.format(contents, params).getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

//    public static <T> void writeJson(File dest, T obj, Class<T> type){
//        try(FileWriter writer = new FileWriter(dest)){
//            GsonHelper.getGson().toJson(obj, type, writer);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    public void writeJson(Path dest, Object obj){
//        try(FileWriter writer = new FileWriter(dest)){
//            GsonHelper.getGson().toJson(obj, writer);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        writeLiteral(dest, GsonHelper.getGson().toJson(obj));
    }

    public void writeLiteral(Path dest, String str){
        ResourceType type;
        Path relative;
        if(dest.startsWith("assets")){
            type = ResourceType.CLIENT_RESOURCES;
            relative = Paths.get("assets").relativize(dest);
        }else{
            type = ResourceType.SERVER_DATA;
            relative = Paths.get("data").relativize(dest);
        }
        DynamicResourcePack.Instance.register(type, relative, str);
    }

    public static void register(String modid){
        ConfigConverter converter = new ConfigConverter(modid, FabricLoader.getInstance().getConfigDirectory());
        new CropWriter(converter).convert();
        new GourdWriter(converter).convert();
        new TreeWriter(converter).convert();
        new WoodWriter(converter).convert();
        new FoodWriter(converter).convert();
        new TagWriter(converter).convert();
        new LangWriter(converter).writeLang();
    }

    public static void main(String[] args){
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url : urls)
            System.out.println(url);
    }

}
