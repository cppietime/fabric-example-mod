package net.funguscow.foodie.mixin;

import net.funguscow.foodie.resource.DynamicResourcePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class ClientResourceMixin {

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resource/ReloadableResourceManager;beginMonitoredReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReloadMonitor;"
    ), index = 3)
    public List<ResourcePack> addDynamic(List<ResourcePack> base){
        List<ResourcePack> list = new ArrayList<>(base);
        list.add(DynamicResourcePack.Instance);
        return list;
    }

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resource/ReloadableResourceManager;beginMonitoredReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReloadMonitor;"
    ), index = 3)
    public List<ResourcePack> reloadDynamic(List<ResourcePack> base){
        List<ResourcePack> list = new ArrayList<>(base);
        list.add(DynamicResourcePack.Instance);
        return list;
    }

}
