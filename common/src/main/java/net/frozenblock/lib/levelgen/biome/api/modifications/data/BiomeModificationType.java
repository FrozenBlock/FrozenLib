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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.MapCodec;

/**
 * Provides a {@link MapCodec} to use for {@link BiomeModification}s on the server and client respectively.
 * @param codec The {@link MapCodec} to use on the logical server.
 * @param clientCodec The {@link MapCodec} to use on the logical client.
 * @param <T> The {@link BiomeModification} class this type represents.
 */
public record BiomeModificationType<T extends BiomeModification>(MapCodec<T> codec, MapCodec<? extends BiomeModification> clientCodec) {

	/**
	 * Creates a {@link BiomeModificationType} that can be deserialized on the logical client.
	 * @param codec The {@link MapCodec} of the represented {@link BiomeModification}.
	 * @return A {@link BiomeModificationType} that can be deserialized on the logical client.
	 * @param <T> The {@link BiomeModification} class to represent.
	 */
	public static <T extends BiomeModification> BiomeModificationType<T> createSyncable(MapCodec<T> codec) {
		return new BiomeModificationType<>(codec, codec);
	}

	/**
	 * Creates a {@link BiomeModificationType} that cannot be deserialized on the logical client.
	 * @param codec The {@link MapCodec} of the represented {@link BiomeModification}.
	 * @return A {@link BiomeModificationType} that cannot be deserialized on the logical client.
	 * @param <T> The {@link BiomeModification} class to represent.
	 */
	public static <T extends BiomeModification> BiomeModificationType<T> createServerOnly(MapCodec<T> codec) {
		return new BiomeModificationType<>(codec, NoOpModification.createDummyCodec());
	}
}
