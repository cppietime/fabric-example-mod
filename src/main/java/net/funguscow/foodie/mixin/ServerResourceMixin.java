package net.funguscow.foodie.mixin;

import net.funguscow.foodie.resource.DynamicResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public class ServerResourceMixin {

//    @Redirect(method = "reloadDataPacks(Lnet/minecraft/world/level/LevelProperties;)V", at = @At(
//            value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;beginReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"
//    ))
//    public CompletableFuture<Unit> extReloadData(ReloadableResourceManager manager, Executor a, Executor b, List<ResourcePack> list, CompletableFuture<Unit> base){
//        list.add(DynamicResourcePack.Instance);
//        return manager.beginReload(a, b, list, base);
//    }

    @ModifyArg(method = "reloadDataPacks(Lnet/minecraft/world/level/LevelProperties;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resource/ReloadableResourceManager;beginReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"
    ), index = 2)
    public List<ResourcePack> addDynamic(List<ResourcePack> base){
        List<ResourcePack> list = new ArrayList<>(base);
        list.add(DynamicResourcePack.Instance);
        return list;
    }

}
