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

package net.frozenblock.lib.wind.api;

import net.frozenblock.lib.wind.impl.networking.WindSyncPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to add custom logic to the {@link WindManager}.
 */
public abstract class WindManagerExtension extends SavedData {
	@Nullable
	private WindManager windManager;

	public void setWindManager(@NotNull WindManager windManager) {
		this.windManager = windManager;
	}

	public @Nullable WindManager getWindManager() {
		return this.windManager;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public static @NotNull String createSaveId(@NotNull ResourceLocation id) {
		return WindManager.WIND_FILE_PATH + "_" + id.getNamespace() + "_" + id.getPath();
	}

	/**
	 * Runs after the baseTick method.
	 */
	abstract void tick(ServerLevel level);

	/**
	 * Runs before the regular tick method.
	 */
	abstract void baseTick(ServerLevel level);

	/**
	 * Used to reset defined values in the rare case of an overflow.
	 *
	 * <p> Please both check and trigger the resets in this method.
	 *
	 * @return whether a reset was needed and run.
	 */
	abstract boolean runResetsIfNeeded();

	/**
	 * Appends custom data to the {@link WindSyncPacket}.
	 *
	 * @param packet The provided {@link WindSyncPacket} to be sent to the client.
	 * @return the updated {@link CustomPacketPayload} with this extension's data.
	 */
	abstract CustomPacketPayload syncPacket(WindSyncPacket packet);
}
