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

package net.frozenblock.lib.config.api.network;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.annotation.LockWhenSynced;
import net.frozenblock.lib.config.api.annotation.UnsyncableEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.4.5
 */
public record ConfigSyncModification<T>(Config<T> config, DataSupplier<T> dataSupplier) implements Consumer<T> {

	@Override
	public void accept(T destination) {
		try {
			T source = dataSupplier.get(config).instance();
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
	public static ModifyType canModifyField(Field field) {
		if (ConfigSyncPacket.notConnected() || ConfigSyncPacket.hasPermissionsToSendSync() || field == null) {
			return ModifyType.CAN_MODIFY;
		}
		if (isSyncable(field)) {
			return ModifyType.LOCKED_AND_SYNCED;
		}
		if (isLockedWhenSynced(field)) {
			return ModifyType.LOCKED_BUT_NOT_SYNCED;
		}
		return ModifyType.CAN_MODIFY;
	}

	@Environment(EnvType.CLIENT)
	public enum ModifyType {
		CAN_MODIFY(true, Optional.empty()),
		LOCKED_BUT_NOT_SYNCED(false, Optional.of(Component.translatable("tooltip.frozenlib.locked_but_not_synced"))),
		LOCKED_AND_SYNCED(false, Optional.of(Component.translatable("tooltip.frozenlib.locked_and_synced")));

		public final boolean canModify;
		public final Optional<Component> tooltip;

		ModifyType(boolean canModify, Optional<Component> tooltip) {
			this.canModify = canModify;
			this.tooltip = tooltip;
		}
	}

}
