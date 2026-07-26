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

package net.frozenblock.lib.config.clothconfig.impl;

import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.platform.api.ClientOnly;

/**
 * Used to integrate config syncing with Cloth Config.
 */
@ClientOnly
public interface DisableableWidgetInterface {
	void frozenLib$addSyncData(ConfigEntry<?> configInstance);
	boolean frozenLib$isSyncable();
	boolean frozenLib$hasValidData();
	ConfigModification.EntryPermissionType frozenLib$getEntryPermissionType();
}
