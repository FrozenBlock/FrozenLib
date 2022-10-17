package net.frozenblock.lib.mixin.server;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.timers.TimerQueue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class)
public class NoExperimentalMixin {

    @Shadow
    @Final
    @Mutable
    private Lifecycle worldGenSettingsLifecycle;

    @Inject(method = "<init>(Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/nbt/CompoundTag;ZIIIFJJIIIZIZZZLnet/minecraft/world/level/border/WorldBorder$Settings;IILjava/util/UUID;Ljava/util/Set;Lnet/minecraft/world/level/timers/TimerQueue;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/world/level/levelgen/WorldGenSettings;Lcom/mojang/serialization/Lifecycle;)V", at = @At("TAIL"))
    private void init(@Nullable DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag, boolean bl, int j, int k, int l, float f, long m, long n, int o, int p, int q, boolean bl2, int r, boolean bl3, boolean bl4, boolean bl5, WorldBorder.Settings settings, int s, int t, @Nullable UUID uUID, Set<String> set, TimerQueue<MinecraftServer> timerQueue, @Nullable CompoundTag compoundTag2, CompoundTag compoundTag3, LevelSettings levelSettings, WorldGenSettings worldGenSettings, Lifecycle lifecycle, CallbackInfo info) {
        this.worldGenSettingsLifecycle = Lifecycle.stable();
    }

    /*@Inject(method = "worldGenSettingsLifecycle", at = @At("HEAD"), cancellable = true)
    public void worldGenSettingsLifecycle(CallbackInfoReturnable<Lifecycle> info) {
        info.cancel();
        info.setReturnValue(Lifecycle.stable());
    }*/

}
