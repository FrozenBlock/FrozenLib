package net.frozenblock.lib.mixin.server;

import net.minecraft.world.flag.FeatureFlagRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = FeatureFlagRegistry.Builder.class, priority = 1001)
public class FeatureFlagBuilderMixin {

	@ModifyConstant(method = "create", constant = @Constant(intValue = 64))
	private int increaseMax(int constant) {
		return Math.max(constant, 512);
	}
}
