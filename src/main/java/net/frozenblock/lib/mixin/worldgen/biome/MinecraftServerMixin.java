package net.frozenblock.lib.mixin.worldgen.biome;

import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Shadow
    @Final
    protected WorldData worldData;

    @Shadow
    @Final
    private RegistryAccess.Frozen registryHolder;

    @Inject(method = "createLevels", at = @At("HEAD"))
    private void addOverworldBiomes(ChunkProgressListener chunkProgressListener,
                                    CallbackInfo ci) {
        this.worldData.worldGenSettings().dimensions().stream().forEach(
                dimensionOptions -> OverworldBiomeData.modifyBiomeSource(
                        this.registryHolder.registryOrThrow(
                                Registry.BIOME_REGISTRY),
                        dimensionOptions.generator().getBiomeSource()));
    }
}
