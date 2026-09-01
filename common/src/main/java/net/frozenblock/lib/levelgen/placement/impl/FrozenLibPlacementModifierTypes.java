/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.placement.impl;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.placement.api.ConfigPlacementFilter;
import net.frozenblock.lib.levelgen.placement.api.NoisePlacementFilter;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibPlacementModifierTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends PlacementModifier>> register = DeferredRegister.create(
			Registries.PLACEMENT_MODIFIER_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_predicate", () -> ConfigPlacementFilter.CODEC);
		register.register("noise_filter", () -> NoisePlacementFilter.CODEC);


		register.register();
	}
}
