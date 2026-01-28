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

package net.frozenblock.lib.config.frozenlib_config;

import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.config.ConfigData;
import net.frozenblock.lib.config.v2.config.ConfigSettings;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.EntryType;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;

public class FrozenLibConfig {
	public static final ConfigData<?> CONFIG = ConfigData.createAndRegister(FrozenLibConstants.config("frozenlib"), ConfigSettings.JSON5_UNQUOTED_KEYS);

	public static final ConfigEntry<Boolean> USE_WIND_ON_NON_FROZEN_SERVERS = CONFIG.entry("useWindOnNonFrozenServers", EntryType.BOOL, true);
	public static final ConfigEntry<Boolean> SAVE_ITEM_COOLDOWNS = CONFIG.entry("saveItemCooldowns", EntryType.BOOL, false);
	public static final ConfigEntry<Boolean> REMOVE_EXPERIMENTAL_WARNING = CONFIG.entry("removeExperimentalWarning", EntryType.BOOL, false);
	public static final ConfigEntry<Boolean> WARDEN_SPAWN_TRACKER_COMMAND = CONFIG.entry("wardenSpawnTrackerCommand", EntryType.BOOL, false);
	public static final ConfigEntry<Boolean> FILE_TRANSFER_SERVER = CONFIG.entry("fileTransferServer", EntryType.BOOL, true);
	public static final ConfigEntry<Boolean> FILE_TRANSFER_CLIENT = CONFIG.entry("fileTransferClient", EntryType.BOOL, true);
	public static final ConfigEntry<FrozenLibModResourcePackApi.PackDownloadSetting> PACK_DOWNLOADING = CONFIG.entry(
		"packDownloading",
		FrozenLibModResourcePackApi.PackDownloadSetting.ENTRY_TYPE,
		FrozenLibModResourcePackApi.PackDownloadSetting.ENABLED
	);
	public static final ConfigEntry<String> CAPE = CONFIG.unsyncableEntry("cape", EntryType.STRING, FrozenLibConstants.string("dummy"));

	// datafixer config
	public static final ConfigEntry<List<String>> DISABLED_DATA_FIX_TYPES = CONFIG.entryBuilder(
		"dataFixer/disabledDataFixTypes",
		EntryType.STRING.asList(),
		List.of("world_gen_settings")
	).comment("Mods can only add to this list. User settings will always apply.").build();
}
