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

package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class BiomeSelectorTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends BiomeSelector>> register = DeferredRegister.create(
			FrozenLibRegistries.BIOME_SELECTOR_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("in_dimension", () -> InDimension.CODEC);
		register.register("in_biomes", () -> InBiomes.CODEC);
		register.register("contains_feature", () -> ContainsFeature.CODEC);
		register.register("contains_placed_feature", () -> ContainsPlacedFeature.CODEC);

		register.register("any_of", () -> AnyOfSelector.CODEC);
		register.register("all_of", () -> AllOfSelector.CODEC);
		register.register("not", () -> NotSelector.CODEC);
		register.register("conditional", () -> ConditionalSelector.CODEC);
		register.register("config_predicate", () -> ConfigSelector.CODEC);

		register.register("true", () -> AlwaysTrueSelector.CODEC);
		register.register("false", () -> AlwaysFalseSelector.CODEC);

		register.register();
	}
}
