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

package net.frozenblock.lib.wind.extension;

import com.mojang.serialization.Codec;
import java.util.List;

import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.WindManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public interface WindManagerExtension {
	Codec<WindManagerExtension> CODEC = FrozenLibRegistries.WIND_MANAGER_EXTENSION_TYPE.byNameCodec().dispatch(WindManagerExtension::type, WindManagerExtensionType::codec);
	StreamCodec<RegistryFriendlyByteBuf, WindManagerExtension> STREAM_CODEC = ByteBufCodecs.registry(FrozenLibRegistries.WIND_MANAGER_EXTENSION_TYPE_REGISTRY)
		.dispatch(WindManagerExtension::type, WindManagerExtensionType::streamCodec);
	Codec<List<WindManagerExtension>> LIST_CODEC = CODEC.listOf();
	StreamCodec<RegistryFriendlyByteBuf, List<WindManagerExtension>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	WindManagerExtensionType<?> type();

	/**
	 * Used to modify the current client instance when synced from the server.
	 */
	<T extends WindManagerExtension> void applyFromSyncedInstance(T extension);

	/**
	 * Runs after the baseTick method.
	 */
	void tick(WindManager windManager, Level level);

	/**
	 * Runs before the regular tick method.
	 */
	void baseTick(WindManager windManager, Level level);

	/**
	 * Used to reset defined values in the rare case of an overflow.
	 *
	 * <p> Please both check and trigger the resets in this method.
	 *
	 * @return whether a reset was needed and run.
	 */
	boolean runResetsIfNeeded();
}
