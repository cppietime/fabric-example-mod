package net.funguscow.foodie.resource;

import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.metadata.PackResourceMetadata;

import java.util.function.Supplier;

public class FoodieClientResourcePackProfile extends ClientResourcePackProfile {

    private DynamicResourcePack mockPack;

    public FoodieClientResourcePackProfile(String name, boolean notSorting, DynamicResourcePack pack, PackResourceMetadata metadata, InsertionPosition direction) {
        super(name, notSorting, () -> pack, pack, metadata, direction);
        mockPack = pack;
    }

    public ResourcePack createResourcePack(){
        return mockPack;
    }
}
