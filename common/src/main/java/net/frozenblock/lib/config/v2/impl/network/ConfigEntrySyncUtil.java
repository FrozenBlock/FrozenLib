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

package net.frozenblock.lib.config.v2.impl.network;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.4
 */
@UtilityClass
public final class ConfigEntrySyncUtil {

	@ApiStatus.Internal
	public static <T> void clearSyncData(ConfigEntry<T> config) {
		if (!ConfigV2Registry.CONFIG_ENTRY.containsValue(config)) throw new IllegalStateException("Config " + config + " not in registry!");
		config.removeSync();
	}

	@ClientOnly
	public static EntryPermissionType canModify(@Nullable ConfigEntry<?> entry) {
		if (entry == null || !entry.isSyncable()) return EntryPermissionType.CAN_MODIFY;

		final boolean isOperator = ConfigEntrySyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false);
		if (!entry.isSynced() || isOperator) return EntryPermissionType.CAN_MODIFY;
		if (entry.isSyncable()) return EntryPermissionType.LOCKED_DUE_TO_SYNC;
		if (entry.isSynced()) return EntryPermissionType.LOCKED_DUE_TO_SERVER;
		return EntryPermissionType.CAN_MODIFY;
	}

	public enum EntryPermissionType {
		CAN_MODIFY(true, Optional.empty(), Optional.empty()),
		LOCKED_FOR_UNKNOWN_REASON(
			false,
			Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_unknown_reason")),
			Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_unknown_reason"))
		),
		LOCKED_DUE_TO_SERVER(
			false,
			Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_server")),
			Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_server_lan"))
		),
		LOCKED_DUE_TO_SYNC(
			false, Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_sync")),
			Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_sync_lan"))
		);

		public final boolean canModify;
		public final Optional<Component> tooltip;
		public final Optional<Component> lanTooltip;

		EntryPermissionType(boolean canModify, Optional<Component> tooltip, Optional<Component> lanTooltip) {
			this.canModify = canModify;
			this.tooltip = tooltip;
			this.lanTooltip = lanTooltip;
		}
	}
}
