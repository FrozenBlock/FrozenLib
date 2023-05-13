/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.surface.mixin.terrablender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrablender.api.SurfaceRuleManager;

@Mixin(SurfaceRuleManager.class)
public class SurfaceRuleManagerMixin {

	@ModifyReturnValue(method = "getNamespacedRules", at = @At("RETURN"))
	private static SurfaceRules.RuleSource getDefaultSurfaceRules(SurfaceRules.RuleSource original, SurfaceRuleManager.RuleCategory category, SurfaceRules.RuleSource fallback) {
		SurfaceRules.RuleSource newRules = FrozenSurfaceRules.getSurfaceRules(
			category == SurfaceRuleManager.RuleCategory.OVERWORLD
				? BuiltinDimensionTypes.OVERWORLD
				: BuiltinDimensionTypes.NETHER
		);

		if (newRules != null) {
			FrozenMain.log("Applying FrozenLib's surface rules to TerraBlender", FrozenMain.UNSTABLE_LOGGING);
			return SurfaceRules.sequence(newRules, original, newRules);
		}
		return original;
	}
}
