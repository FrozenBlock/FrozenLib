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

package net.frozenblock.lib.worldgen.biome.api;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class NetherFrozenBiome extends FrozenBiome {

	@Override
	public final float temperature() {
		return 2F;
	}

	@Override
	public final float downfall() {
		return 0F;
	}

	@Override
	public final boolean hasPrecipitation() {
		return false;
	}

	@Override
	public final int waterColor() {
		return 4159204;
	}

	@Contract(pure = true)
	@Override
	public final @Nullable Integer foliageColorOverride() {
		return null;
	}

	@Contract(pure = true)
	@Override
	public final @Nullable Integer dryFoliageColorOverride() {
		return null;
	}

	@Contract(pure = true)
	@Override
	public final @Nullable Integer grassColorOverride() {
		return null;
	}

	@Override
	public final void injectToOverworld(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> parameters) {
	}

}
