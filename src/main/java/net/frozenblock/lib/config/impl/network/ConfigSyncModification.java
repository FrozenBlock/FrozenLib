/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.impl.network;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.annotation.LockWhenSynced;
import net.frozenblock.lib.config.api.annotation.UnsyncableEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.network.ConfigSyncData;
import net.frozenblock.lib.networking.FrozenNetworking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.5
 */
public record ConfigSyncModification<T>(Config<T> config, DataSupplier<T> dataSupplier) implements Consumer<T> {

	@Override
	public void accept(T destination) {
		try {
			ConfigSyncData<T> syncData = dataSupplier.get(config);
			if (syncData == null) return;
			T source = syncData.instance();
			ConfigModification.copyInto(source, destination, true);
		} catch (NullPointerException ignored) {}
	}

	@FunctionalInterface
	public interface DataSupplier<T> {
		ConfigSyncData<T> get(Config<T> config);
	}

	public static boolean isSyncable(@NotNull Field field) {
		return !field.isAnnotationPresent(UnsyncableEntry.class);
	}

	public static boolean isLockedWhenSynced(@NotNull Field field) {
		return field.isAnnotationPresent(LockWhenSynced.class);
	}

	@Environment(EnvType.CLIENT)
	public static ConfigModification.EntryPermissionType canModifyField(@Nullable Field field, @Nullable Config<?> config) {
		if (config != null && field != null && config.supportsSync()) {
			boolean isOperator = FrozenNetworking.connectedToIntegratedServer() || ConfigSyncPacket.hasPermissionsToSendSync();
			if (!config.isSynced() || isOperator) {
				return ConfigModification.EntryPermissionType.CAN_MODIFY;
			} else if (isSyncable(field)) {
				return ConfigModification.EntryPermissionType.LOCKED_DUE_TO_SYNC;
			} else if (isLockedWhenSynced(field)) {
				return ConfigModification.EntryPermissionType.LOCKED_DUE_TO_SERVER;
			}
		}
		return ConfigModification.EntryPermissionType.CAN_MODIFY;
	}

}
