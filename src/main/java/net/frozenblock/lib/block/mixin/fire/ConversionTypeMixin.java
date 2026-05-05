package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConversionType.class)
public class ConversionTypeMixin {

	@Inject(method = "convertCommon", at = @At("HEAD"))
	private static void frozenLib$setFireTypeOnConversion(Mob from, Mob to, ConversionParams params, CallbackInfo info) {
		final FireData fireData = from.getAttached(FireData.ATTACHMENT);
		if (fireData == null) return;
		to.setAttached(FireData.ATTACHMENT, new FireData(fireData.type(), fireData.permanent()));
	}
}
