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

package net.frozenblock.lib.worldgen.biome.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.worldgen.biome.impl.BiomeInterface;
import net.frozenblock.lib.worldgen.biome.impl.FrozenGrassColorModifier;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeInterface {
	@Unique
	private FrozenGrassColorModifier frozenLib$frozenGrassColorModifier;

	@ModifyReturnValue(
		method = "getGrassColor",
		at = @At(
			value = "RETURN"
		)
	)
	public int frozenLib$modifyGrassColor(int original, double x, double y) {
		if (this.frozenLib$frozenGrassColorModifier != null) {
			return this.frozenLib$frozenGrassColorModifier.modifyGrassColor(x, y, original);
		}
		return original;
	}

	@Override
	public void frozenLib$setFrozenGrassColorModifier(FrozenGrassColorModifier frozenGrassColorModifier) {
		this.frozenLib$frozenGrassColorModifier = frozenGrassColorModifier;
	}

	@Override
	public FrozenGrassColorModifier frozenLib$getFrozenGrassColorModifier() {
		return this.frozenLib$frozenGrassColorModifier;
	}
}
