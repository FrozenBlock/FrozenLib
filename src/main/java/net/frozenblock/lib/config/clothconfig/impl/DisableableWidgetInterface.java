/*
 * Copyright (C) 2024 FrozenBlock
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;

/**
 * Used to integrate config syncing with Cloth Config.
 */
@Environment(EnvType.CLIENT)
public interface DisableableWidgetInterface {

	void frozenLib$addSyncData(Class<?> clazz, String identifier, Config<?> configInstance);

	boolean frozenLib$isSyncable();

	boolean frozenLib$hasValidData();

	ConfigModification.EntryPermissionType frozenLib$getEntryPermissionType();
}
