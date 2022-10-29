package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.feature_flag.api.FrozenFeatureFlags;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FeatureFlags.class)
public class FeatureFlagsMixin {

	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/flag/FeatureFlagRegistry$Builder;build()Lnet/minecraft/world/flag/FeatureFlagRegistry;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void featureFlagEntrypoint(CallbackInfo ci, FeatureFlagRegistry.Builder builder) {
		FrozenFeatureFlags.ON_FEATURE_FLAG_INIT.invoker().init(builder);
	}
}
