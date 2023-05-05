package net.frozenblock.lib.worldgen.surface.mixin.terrablender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrablender.api.SurfaceRuleManager;

@Mixin(SurfaceRuleManager.class)
public class SurfaceRuleManagerMixin {

	@ModifyReturnValue(method = "getDefaultSurfaceRules", at = @At("RETURN"))
	private static SurfaceRules.RuleSource getDefaultSurfaceRules(SurfaceRules.RuleSource original, SurfaceRuleManager.RuleCategory category) {
		SurfaceRules.RuleSource newRules;
		if (category == SurfaceRuleManager.RuleCategory.OVERWORLD) {
			newRules = FrozenSurfaceRules.getSurfaceRules(BuiltinDimensionTypes.OVERWORLD);
		} else {
			newRules = FrozenSurfaceRules.getSurfaceRules(BuiltinDimensionTypes.NETHER);
		}

		if (newRules != null) {
			return SurfaceRules.sequence(original, newRules);
		}
		return original;
	}
}
