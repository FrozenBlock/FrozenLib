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

package net.frozenblock.lib.config.impl.network;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.frozenblock.lib.config.api.sync.network.ConfigSyncData;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
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
			if (syncData == null || !FrozenNetworking.connectedToServer()) {
				new Exception("Attempted to sync config " + config.path() + " for mod " + config.modId() + " outside a server!").printStackTrace();
				return;
			}
			T source = syncData.instance();
			config.setSynced(true);
			ConfigModification.copyInto(source, destination, true);
		} catch (NullPointerException ignored) {}
	}

	@ApiStatus.Internal
	public static <T> void clearSyncData(Config<T> config) {
		if (!ConfigRegistry.contains(config)) throw new IllegalStateException("Config " + config + " not in registry!");
		ConfigRegistry.removeSyncData(config);
		ConfigRegistry.getModificationsForConfig(config).keySet().removeIf(key -> key.modification() instanceof ConfigSyncModification<?>);
		config.setSynced(false);
	}

	@FunctionalInterface
	public interface DataSupplier<T> {
		ConfigSyncData<T> get(Config<T> config);
	}

	public static boolean isSyncable(@NotNull Field field) {
		EntrySyncData entrySyncData = field.getAnnotation(EntrySyncData.class);
		return entrySyncData == null || entrySyncData.behavior().canSync();
	}

	public static boolean isLockedWhenSynced(@NotNull Field field) {
		EntrySyncData entrySyncData = field.getAnnotation(EntrySyncData.class);
		return entrySyncData != null && entrySyncData.behavior() == SyncBehavior.LOCK_WHEN_SYNCED;
	}

	@Environment(EnvType.CLIENT)
	public static ConfigModification.EntryPermissionType canModifyField(@Nullable Field field, @Nullable Config<?> config) {
		if (config != null && field != null && config.supportsSync()) {
			boolean isOperator = ConfigSyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false);
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
