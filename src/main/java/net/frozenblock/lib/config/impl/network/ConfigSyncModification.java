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

package net.frozenblock.lib.config.impl.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.4
 */
public class ConfigSyncModification {

	@ApiStatus.Internal
	public static <T> void clearSyncData(ConfigEntry<T> config) {
		if (!ConfigV2Registry.CONFIG_ENTRY.containsValue(config)) throw new IllegalStateException("Config " + config + " not in registry!");
		config.removeSync();
	}

	@Environment(EnvType.CLIENT)
	public static ConfigModification.EntryPermissionType canModify(@Nullable ConfigEntry<?> entry) {
		if (entry == null || !entry.isSyncable()) return ConfigModification.EntryPermissionType.CAN_MODIFY;

		final boolean isOperator = ConfigEntrySyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false);
		if (!entry.isSynced() || isOperator) return ConfigModification.EntryPermissionType.CAN_MODIFY;
		if (entry.isSyncable()) return ConfigModification.EntryPermissionType.LOCKED_DUE_TO_SYNC;
		if (entry.isSynced()) return ConfigModification.EntryPermissionType.LOCKED_DUE_TO_SERVER;
		return ConfigModification.EntryPermissionType.CAN_MODIFY;
	}

}
