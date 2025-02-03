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

package net.frozenblock.lib.config.frozenlib_config;

import blue.endless.jankson.Comment;
import java.util.List;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes;

public class FrozenLibConfig {

	public static final Config<FrozenLibConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			FrozenLibConstants.MOD_ID,
			FrozenLibConfig.class,
			JsonType.JSON5_UNQUOTED_KEYS,
			true,
			QuiltDataFixes.buildFixer(new QuiltDataFixerBuilder(0)),
			0
		) {
			@Override
			public void onSave() throws Exception {
				super.onSave();
				this.onSync(null);
			}

			@Override
			public void onSync(FrozenLibConfig syncInstance) {
				var config = this.config();
				USE_WIND_ON_NON_FROZEN_SERVERS = config.useWindOnNonFrozenServers;
				FILE_TRANSFER_SERVER = config.fileTransferServer;
				FILE_TRANSFER_CLIENT = config.fileTransferClient;
				IS_DEBUG = config.isDebug && FabricLoader.getInstance().isDevelopmentEnvironment();
			}
		}
	);

	public static volatile boolean USE_WIND_ON_NON_FROZEN_SERVERS = true;
	public static volatile boolean FILE_TRANSFER_SERVER = true;
	public static volatile boolean FILE_TRANSFER_CLIENT = true;
	public static volatile boolean IS_DEBUG = false;

	@Comment("Mods may override any of these options, but the config file will not change.")

	@EntrySyncData("isDebug")
	public boolean isDebug = false;

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

	@EntrySyncData(value = "cape", behavior = SyncBehavior.UNSYNCABLE)
	public String cape = FrozenLibConstants.string("dummy");

	@ConfigEntry.Gui.CollapsibleObject
	public final DataFixerConfig dataFixer = new DataFixerConfig();

	public static class DataFixerConfig {

		@Comment("Mods can only add to this list. User settings will always apply.")
		@EntrySyncData("disabledDataFixTypes")
		public List<String> disabledDataFixTypes = List.of(
			"world_gen_settings"
		);
	}

	public static FrozenLibConfig get(boolean real) {
		if (real)
			return INSTANCE.instance();

		return INSTANCE.config();
	}

	public static FrozenLibConfig get() {
		return get(false);
	}

	public static FrozenLibConfig getWithSync() {
		return INSTANCE.configWithSync();
	}
}
