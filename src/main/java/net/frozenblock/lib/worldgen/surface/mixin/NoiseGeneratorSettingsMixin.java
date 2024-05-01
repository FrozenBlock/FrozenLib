/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.surface.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NoiseGeneratorSettings.class, priority = 990) // Apply before default mods
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorInterface {

	/**
	 * Surface rules added by FrozenLib
	 */
	@Unique
	private SurfaceRules.RuleSource frozenLib$frozenSurfaceRules;

	@ModifyReturnValue(method = "surfaceRule", at = @At("RETURN"))
	private SurfaceRules.RuleSource frozenLib$modifyRules(SurfaceRules.RuleSource original) {
		if (this.frozenLib$frozenSurfaceRules != null) {
			return SurfaceRules.sequence(this.frozenLib$frozenSurfaceRules, original);
		}

		return original;
	}

	@Unique
	@Override
	public void writeSurfaceRules(SurfaceRules.RuleSource surfaceRule) {
		if (surfaceRule == null || surfaceRule == this.frozenLib$frozenSurfaceRules) return;

		this.frozenLib$frozenSurfaceRules = surfaceRule;
	}

}
