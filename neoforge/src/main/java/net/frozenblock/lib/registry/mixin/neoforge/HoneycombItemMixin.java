package net.frozenblock.lib.registry.mixin.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

	@Inject(method = "lambda$static$0", at = @At("RETURN"), cancellable = true)
	private static void frozenLib$makeWaxableMapMutable(CallbackInfoReturnable<BiMap> cir) {
		cir.setReturnValue(HashBiMap.create(cir.getReturnValue()));
	}
}
