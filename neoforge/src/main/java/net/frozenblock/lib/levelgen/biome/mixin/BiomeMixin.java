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
import net.frozenblock.lib.levelgen.biome.mixin.neoforge.ModifiableBiomeInfoAccessor;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeInterface { // In common mixins.json
	@Mutable
	@Shadow
	@Final
	private ModifiableBiomeInfo modifiableBiomeInfo;

	@Shadow
	public abstract ModifiableBiomeInfo modifiableBiomeInfo();

	@Mutable
	@Shadow
	@Final
	private EnvironmentAttributeMap attributes;
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

	@Unique
	@Override
	public void frozenLib$setFrozenLibGrassColorModifier(FrozenLibGrassColorModifier modifier) {
		this.frozenLib$frozenLibGrassColorModifier = modifier;
	}

	@Unique
	@Override
	public FrozenLibGrassColorModifier frozenLib$getFrozenLibGrassColorModifier() {
		return this.frozenLib$frozenLibGrassColorModifier;
	}

	@Unique
	@Override
	public EnvironmentAttributeMap frozenLib$attributes() {
		return this.modifiableBiomeInfo().get().attributes();
	}

	@Unique
	@Override
	public void frozenLib$setAttributes(EnvironmentAttributeMap attributes) {
		this.attributes = attributes;

		final ModifiableBiomeInfoAccessor accessor = (ModifiableBiomeInfoAccessor) this.modifiableBiomeInfo();

		final ModifiableBiomeInfo.BiomeInfo modifiedInfo = this.modifiableBiomeInfo.getModifiedBiomeInfo();
		if (modifiedInfo != null) {
			accessor.frozenLib$setModifiedBiomeInfo(
				new ModifiableBiomeInfo.BiomeInfo(
					modifiedInfo.climateSettings(),
					attributes,
					modifiedInfo.effects(),
					modifiedInfo.generationSettings()
				)
			);
		} else {
			final ModifiableBiomeInfo.BiomeInfo originalInfo = this.modifiableBiomeInfo().getOriginalBiomeInfo();
			this.modifiableBiomeInfo = new ModifiableBiomeInfo(
				new ModifiableBiomeInfo.BiomeInfo(
					originalInfo.climateSettings(),
					attributes,
					originalInfo.effects(),
					originalInfo.generationSettings()
				)
			);
		}
	}

	@Unique
	@Override
	public Biome.ClimateSettings frozenLib$getClimateSettings() {
		return this.modifiableBiomeInfo().get().climateSettings();
	}

	@Unique
	@Override
	public void frozenLib$setClimateSettings(Biome.ClimateSettings settings) {
		this.climateSettings = settings;

		final ModifiableBiomeInfoAccessor accessor = (ModifiableBiomeInfoAccessor) this.modifiableBiomeInfo();

		final ModifiableBiomeInfo.BiomeInfo modifiedInfo = this.modifiableBiomeInfo.getModifiedBiomeInfo();
		if (modifiedInfo != null) {
			accessor.frozenLib$setModifiedBiomeInfo(
				new ModifiableBiomeInfo.BiomeInfo(
					settings,
					modifiedInfo.attributes(),
					modifiedInfo.effects(),
					modifiedInfo.generationSettings()
				)
			);
		} else {
			final ModifiableBiomeInfo.BiomeInfo originalInfo = this.modifiableBiomeInfo().getOriginalBiomeInfo();
			this.modifiableBiomeInfo = new ModifiableBiomeInfo(
				new ModifiableBiomeInfo.BiomeInfo(
					settings,
					originalInfo.attributes(),
					originalInfo.effects(),
					originalInfo.generationSettings()
				)
			);
		}
	}
}
