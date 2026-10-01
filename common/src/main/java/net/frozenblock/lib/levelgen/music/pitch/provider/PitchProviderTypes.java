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

		register.register("biome", () -> BiomeProvider.CODEC);
		register.register("structure", () -> StructureProvider.CODEC);
		register.register("dimension", () -> DimensionProvider.CODEC);
		register.register("constant", () -> Constant.CODEC);
		register.register("add", () -> Add.CODEC);
		register.register("subtract", () -> Subtract.CODEC);
		register.register("multiply", () -> Multiply.CODEC);
		register.register("divide", () -> Divide.CODEC);
		register.register("sine", () -> Sine.CODEC);
		register.register("cosine", () -> Cosine.CODEC);
		register.register("clamp", () -> ClampedPitch.CODEC);
		register.register("lerp", () -> LerpedPitch.CODEC);
		register.register("config_predicate", () -> ConfigPredicatePitch.CODEC);
		register.register("config_predicate_selector", () -> ConfigPredicateSelectorPitch.CODEC);

		register.register();
	}

	private PitchProviderTypes() {}
}
