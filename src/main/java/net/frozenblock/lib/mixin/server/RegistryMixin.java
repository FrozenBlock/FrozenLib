package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.events.api.RegistryFreezeEvents;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public class RegistryMixin {

	@Inject(method = "freezeBuiltins", at = @At("HEAD"))
	private static void freezeBuiltinsStart(CallbackInfo ci) {
		RegistryFreezeEvents.START_REGISTRY_FREEZE.invoker().onStartRegistryFreeze(null, true);
	}

	@Inject(method = "freezeBuiltins", at = @At("TAIL"))
	private static void freezeBuiltinsEnd(CallbackInfo ci) {
		RegistryFreezeEvents.END_REGISTRY_FREEZE.invoker().onEndRegistryFreeze(null, true);
	}
}
