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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
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
	private ResourceKey<DimensionType> frozenLib$dimension;

	@ModifyReturnValue(method = "surfaceRule", at = @At("RETURN"))
	private SurfaceRules.RuleSource frozenLib$modifyRules(SurfaceRules.RuleSource original) {

		if (this.frozenLib$dimension != null) {
			if (this.frozenLib$frozenSurfaceRules == null) {
				SurfaceRules.RuleSource frozenSurfaceRules = FrozenSurfaceRules.getSurfaceRules(this.frozenLib$dimension);

				if (frozenSurfaceRules != null) {
					this.frozenLib$frozenSurfaceRules = frozenSurfaceRules;
				}
			}

			if (this.frozenLib$frozenSurfaceRules != null) {
				return SurfaceRules.sequence(this.frozenLib$frozenSurfaceRules, original);
			}
		}
		return original;
	}

	@Inject(method = "overworld", at = @At("RETURN"))
	private static void overworld(boolean amplified, boolean largeBiomes, CallbackInfoReturnable<NoiseGeneratorSettings> cir) {
		NoiseGeneratorSettings settings = cir.getReturnValue();

		NoiseGeneratorInterface.class.cast(settings).setDimension(BuiltinDimensionTypes.OVERWORLD);
	}

	@Inject(method = "nether", at = @At("RETURN"))
	private static void nether(CallbackInfoReturnable<NoiseGeneratorSettings> cir) {
		NoiseGeneratorSettings settings = cir.getReturnValue();

		NoiseGeneratorInterface.class.cast(settings).setDimension(BuiltinDimensionTypes.NETHER);
	}

	@Unique
	@Override
	public void setDimension(ResourceKey<DimensionType> dimension) {
		this.frozenLib$dimension = dimension;
	}

}
