/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.impl.network;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.frozenblock.lib.config.api.sync.network.ConfigSyncData;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
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
