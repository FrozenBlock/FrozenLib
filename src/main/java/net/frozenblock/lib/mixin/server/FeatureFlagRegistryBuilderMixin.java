package net.frozenblock.lib.mixin.server;

import net.minecraft.world.flag.FeatureFlagRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FeatureFlagRegistry.Builder.class)
public class FeatureFlagRegistryBuilderMixin {

	@ModifyConstant(method = "create", constant = @Constant(intValue = 64), require = 0)
	private int create(int constant) {
		return Integer.MAX_VALUE;
	}
}
