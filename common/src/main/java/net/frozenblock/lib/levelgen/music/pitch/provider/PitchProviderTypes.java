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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibEarlyConstants;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PitchProviderTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends PitchProvider>> register = DeferredRegister.create(
			FrozenLibRegistries.PITCH_PROVIDER_TYPE_REGISTRY,
			FrozenLibEarlyConstants.MOD_ID
		);

		register.register("biome_requirement", () -> BiomeRequirement.MAP_CODEC);
		register.register("structure_requirement", () -> StructureRequirement.MAP_CODEC);
		register.register("dimension_requirement", () -> DimensionRequirement.MAP_CODEC);
		register.register("config_predicate_requirement", () -> ConfigRequirement.MAP_CODEC);

		register.register("abs", () -> Absolute.MAP_CODEC);
		register.register("avg", () -> Average.MAP_CODEC);
		register.register("ceil", () -> Ceiling.MAP_CODEC);
		register.register("conditional", () -> ConditionalValue.MAP_CODEC);
		register.register("constant", () -> ConstantValue.MAP_CODEC);
		register.register("cos", () -> Cosine.MAP_CODEC);
		register.register("sub", () -> Difference.MAP_CODEC);
		register.register("environment_attribute", () -> EnvironmentAttributeValue.MAP_CODEC);
		register.register("floor", () -> Floor.MAP_CODEC);
		register.register("length", () -> Length.MAP_CODEC);
		register.register("max", () -> Maximum.MAP_CODEC);
		register.register("min", () -> Minimum.MAP_CODEC);
		register.register("mod", () -> Modulus.MAP_CODEC);
		register.register("negate", () -> Negate.MAP_CODEC);
		register.register("pow", () -> Power.MAP_CODEC);
		register.register("mul", () -> Product.MAP_CODEC);
		register.register("div", () -> Quotient.MAP_CODEC);
		register.register("round", () -> Round.MAP_CODEC);
		register.register("sin", () -> Sine.MAP_CODEC);
		register.register("sqrt", () -> SquareRoot.MAP_CODEC);
		register.register("add", () -> Sum.MAP_CODEC);
		register.register("truncate", () -> Truncate.MAP_CODEC);

		register.register();
	}

	private PitchProviderTypes() {}
}
