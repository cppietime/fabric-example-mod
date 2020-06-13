package net.funguscow.foodie.injection;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.util.SignType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SignTypeInject {

    private static final Set<SignType> TYPES = new ObjectArraySet<>();
    private static final Map<String, Integer> COLORS = new HashMap<>();

    public static SignType addType(String name){
        try{
            Constructor<SignType> typeConstructor = SignType.class.getDeclaredConstructor(String.class);
            String[] comp = name.split(",");
            SignType signType = typeConstructor.newInstance(comp[0]);
            Field field = SignType.class.getDeclaredField("VALUES");
            field.setAccessible(true);
            Set<SignType> values = (Set<SignType>)field.get(null);
            values.add(signType);
            TYPES.add(signType);
            COLORS.put(comp[0], Integer.parseInt(comp[1], 16));
            return signType;
        }catch(Exception e){
            e.printStackTrace();
            return SignType.OAK;
        }
    }

    public static Integer getColor(SignType key){
        if(!TYPES.contains(key))
            return null;
        return COLORS.get(key.getName());
    }
}
