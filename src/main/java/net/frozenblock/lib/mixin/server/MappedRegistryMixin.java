package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.events.api.RegistryFreezeEvents;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {

	@Inject(method = "freeze", at = @At("HEAD"))
	private void freezeStart(CallbackInfoReturnable<Registry<T>> cir) {
		RegistryFreezeEvents.START_REGISTRY_FREEZE.invoker().onStartRegistryFreeze(MappedRegistry.class.cast(this), false);
	}

	@Inject(method = "freeze", at = @At("TAIL"))
	private void freezeEnd(CallbackInfoReturnable<Registry<T>> cir) {
		RegistryFreezeEvents.END_REGISTRY_FREEZE.invoker().onEndRegistryFreeze(MappedRegistry.class.cast(this), false);
	}
}
