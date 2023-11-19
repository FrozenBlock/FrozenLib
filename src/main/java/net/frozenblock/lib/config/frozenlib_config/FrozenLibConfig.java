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

package net.frozenblock.lib.config.frozenlib_config;

import blue.endless.jankson.Comment;
import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes;

public class FrozenLibConfig {

	public static final Config<FrozenLibConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			FrozenSharedConstants.MOD_ID,
			FrozenLibConfig.class,
			JsonType.JSON5_UNQUOTED_KEYS,
			true,
			QuiltDataFixes.buildFixer(new QuiltDataFixerBuilder(0)),
			0
		)
	);

	@Comment("Mods may override any of these options, but the config file will not change.")

	public boolean useWindOnNonFrozenServers = true;

	public boolean saveItemCooldowns = false;

	public boolean removeExperimentalWarning = false;

	public boolean wardenSpawnTrackerCommand = false;

	@ConfigEntry.Gui.CollapsibleObject
	public final DataFixerConfig dataFixer = new DataFixerConfig();

	public static class DataFixerConfig {

		@Comment("Mods can only add to this list. User settings will always apply.")
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
}
