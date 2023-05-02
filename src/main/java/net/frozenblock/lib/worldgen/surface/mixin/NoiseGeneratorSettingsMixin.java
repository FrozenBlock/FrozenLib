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

package net.frozenblock.lib.worldgen.surface.mixin;

import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorInterface {

	/**
	 * Surface rules added by FrozenLib
	 */
	@Unique
	private SurfaceRules.RuleSource frozenLib$frozenSurfaceRules;

	@Unique
	private Holder<DimensionType> frozenLib$dimension;

	@Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
	private void frozenLib$modifyRules(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		SurfaceRules.RuleSource returnValue = cir.getReturnValue();

		if (this.frozenLib$dimension != null) {
			var optionalKey = this.frozenLib$dimension.unwrapKey();

			if (this.frozenLib$frozenSurfaceRules == null && optionalKey.isPresent()) {
				var key = optionalKey.get();
				SurfaceRules.RuleSource frozenSurfaceRules = FrozenSurfaceRules.getSurfaceRules(key);

				if (frozenSurfaceRules != null) {
					this.frozenLib$frozenSurfaceRules = frozenSurfaceRules;
				}
			}

			if (this.frozenLib$frozenSurfaceRules != null) {
				cir.setReturnValue(SurfaceRules.sequence(this.frozenLib$frozenSurfaceRules, returnValue));
			}
		}
	}

	@Unique
	@Override
	public void setDimension(Holder<DimensionType> dimension) {
		this.frozenLib$dimension = dimension;
	}

}
