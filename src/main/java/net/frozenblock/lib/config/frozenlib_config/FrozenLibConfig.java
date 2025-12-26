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

import blue.endless.jankson.Comment;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.config.ConfigSettings;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.entry.EntryType;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.minecraft.resources.Identifier;

public class FrozenLibConfig {

	static {
		ConfigData.createAndRegister(FrozenLibConstants.id("frozenlib"), ConfigSettings.JSON5_UNQUOTED_KEYS);
	}

	public static final ConfigEntry<Boolean> USE_WIND_ON_NON_FROZEN_SERVERS_ENTRY = new ConfigEntry<>(id("use_wind_on_non_frozen_servers"), EntryType.BOOL, true);

	public static final ConfigEntry<Boolean> SAVE_ITEM_COOLDOWNS = new ConfigEntry<>(id("save_item_cooldowns"), EntryType.BOOL, false);

	public static final ConfigEntry<Boolean> REMOVE_EXPERIMENTAL_WARNING = new ConfigEntry<>(id("remove_experimental_warning"), EntryType.BOOL, false);

	public static final ConfigEntry<Boolean> WARDEN_SPAWN_TRACKER_COMMAND = new ConfigEntry<>(id("warden_spawn_tracker_command"), EntryType.BOOL, false);

	public static final ConfigEntry<Boolean> FILE_TRANSFER_SERVER_ENTRY = new ConfigEntry<>(id("file_transfer_server"), EntryType.BOOL, true);

	public static final ConfigEntry<Boolean> FILE_TRANSFER_CLIENT_ENTRY = new ConfigEntry<>(id("file_transfer_client"), EntryType.BOOL, true);

	// TODO packDownloads

	public static final ConfigEntry<String> CAPE = new ConfigEntry<>(id("cape"), EntryType.STRING, FrozenLibConstants.string("dummy"));

	// datafixer config
	public static final ConfigEntry<List<String>> DISABLED_DATA_FIX_TYPES = ConfigEntry.builder(
		id("datafixer/disabled_data_fix_types"), EntryType.STRING.asList(),
		List.of("world_gen_settings")
	).comment("Mods can only add to this list. User settings will always apply.").build();

	public static final Config<FrozenLibConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			FrozenLibConstants.MOD_ID,
			FrozenLibConfig.class,
			JsonType.JSON5_UNQUOTED_KEYS,
			true
		) {
			@Override
			public void onSave() throws Exception {
				super.onSave();
				this.onSync(null);
			}

			@Override
			public void onSync(FrozenLibConfig syncInstance) {
				var config = this.config();
				USE_WIND_ON_NON_FROZEN_SERVERS = USE_WIND_ON_NON_FROZEN_SERVERS_ENTRY.get();
				FILE_TRANSFER_SERVER = config.fileTransferServer;
				FILE_TRANSFER_CLIENT = config.fileTransferClient;
			}
		}
	);

	public static volatile boolean USE_WIND_ON_NON_FROZEN_SERVERS = true;
	public static volatile boolean FILE_TRANSFER_SERVER = true;
	public static volatile boolean FILE_TRANSFER_CLIENT = true;

	@Comment("Mods may override any of these options, but the config file will not change.")

	@EntrySyncData(value = "useWindOnNonFrozenServers", behavior = SyncBehavior.UNSYNCABLE)
	public boolean useWindOnNonFrozenServers = true;

	@EntrySyncData("saveItemCooldowns")
	public boolean saveItemCooldowns = false;

	@EntrySyncData(value = "removeExperimentalWarning", behavior = SyncBehavior.UNSYNCABLE)
	public boolean removeExperimentalWarning = false;

	@EntrySyncData("wardenSpawnTrackerCommand")
	public boolean wardenSpawnTrackerCommand = false;

	@EntrySyncData("fileTransferServer")
	public boolean fileTransferServer = true;

	@EntrySyncData(value = "fileTransferClient", behavior = SyncBehavior.UNSYNCABLE)
	public boolean fileTransferClient = true;

	@EntrySyncData(value = "packDownloading", behavior = SyncBehavior.UNSYNCABLE)
	public FrozenLibModResourcePackApi.PackDownloadSetting packDownloading = FrozenLibModResourcePackApi.PackDownloadSetting.ENABLED;

	@EntrySyncData(value = "cape", behavior = SyncBehavior.UNSYNCABLE)
	public String cape = FrozenLibConstants.string("dummy");

	public final DataFixerConfig dataFixer = new DataFixerConfig();

	public static class DataFixerConfig {
		@Comment("Mods can only add to this list. User settings will always apply.")
		@EntrySyncData("disabledDataFixTypes")
		public List<String> disabledDataFixTypes = List.of(
			"world_gen_settings"
		);
	}

	public static FrozenLibConfig get(boolean real) {
		if (real) return INSTANCE.instance();
		return INSTANCE.config();
	}

	public static FrozenLibConfig get() {
		return get(false);
	}

	public static FrozenLibConfig getWithSync() {
		return INSTANCE.configWithSync();
	}

	private static Identifier id(String path) {
		return FrozenLibConstants.id("frozenlib/" + path);
	}
}
