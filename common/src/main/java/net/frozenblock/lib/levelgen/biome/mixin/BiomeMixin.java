/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.biome.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.levelgen.biome.impl.FrozenLibGrassColorModifier;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeInterface;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeInterface {
	@Mutable
	@Shadow
	@Final
	private Biome.ClimateSettings climateSettings;

	@Unique
	private FrozenLibGrassColorModifier frozenLib$frozenLibGrassColorModifier;

	@ModifyReturnValue(method = "getGrassColor", at = @At("RETURN"))
	public int frozenLib$modifyGrassColor(int original, double x, double z) {
		if (this.frozenLib$frozenLibGrassColorModifier != null) return this.frozenLib$frozenLibGrassColorModifier.modifyGrassColor(x, z, original);
		return original;
	}

	@Override
	public void frozenLib$setFrozenLibGrassColorModifier(FrozenLibGrassColorModifier modifier) {
		this.frozenLib$frozenLibGrassColorModifier = modifier;
	}

	@Override
	public FrozenLibGrassColorModifier frozenLib$getFrozenLibGrassColorModifier() {
		return this.frozenLib$frozenLibGrassColorModifier;
	}

	@Override
	public Biome.ClimateSettings frozenLib$getClimateSettings() {
		return this.climateSettings;
	}

	@Override
	public void frozenLib$setClimateSettings(Biome.ClimateSettings settings) {
		this.climateSettings = settings;
	}
}
