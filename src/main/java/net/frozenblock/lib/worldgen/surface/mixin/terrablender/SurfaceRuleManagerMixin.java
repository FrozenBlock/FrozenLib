/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.surface.mixin.terrablender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrablender.api.SurfaceRuleManager;

@Mixin(SurfaceRuleManager.class)
public class SurfaceRuleManagerMixin {

	@ModifyReturnValue(method = "getNamespacedRules", at = @At("RETURN"))
	private static SurfaceRules.RuleSource frozenLib$getDefaultSurfaceRules(
		SurfaceRules.RuleSource original, SurfaceRuleManager.RuleCategory category, SurfaceRules.RuleSource fallback
	) {
		SurfaceRules.RuleSource newRules = FrozenSurfaceRules.getSurfaceRules(
			category == SurfaceRuleManager.RuleCategory.OVERWORLD
				? BuiltinDimensionTypes.OVERWORLD
				: BuiltinDimensionTypes.NETHER
		);

		if (newRules != null) {
			FrozenLogUtils.log("Applying FrozenLib's surface rules to TerraBlender", FrozenLibConstants.UNSTABLE_LOGGING);
			return SurfaceRules.sequence(newRules, original, newRules);
		}
		return original;
	}
}
