package net.frozenblock.lib.registry.mixin.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.level.block.WeatheringCopper;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {

	@Inject(method = "lambda$static$0", at = @At("RETURN"), cancellable = true)
	private static void frozenLib$makeOxidationMapMutable(CallbackInfoReturnable<BiMap> cir) {
		cir.setReturnValue(HashBiMap.create(cir.getReturnValue()));
	}
}
