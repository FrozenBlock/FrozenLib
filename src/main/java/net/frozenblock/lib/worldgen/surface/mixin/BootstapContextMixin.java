package net.frozenblock.lib.worldgen.surface.mixin;

import net.frozenblock.lib.worldgen.surface.impl.SetNoiseGeneratorPresetInterface;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BootstapContext.class)
public interface BootstapContextMixin<T> {

	@Inject(method = {"register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;)Lnet/minecraft/core/Holder$Reference;"}, at = @At("HEAD"))
	default void register(ResourceKey<T> registryKey, T value, CallbackInfoReturnable<net.minecraft.core.Holder.Reference<T>> info) {
		if (value instanceof NoiseGeneratorSettings noiseGeneratorSettings) {
			SetNoiseGeneratorPresetInterface.class.cast(noiseGeneratorSettings).setPreset(registryKey.location());
		}
	}

}
