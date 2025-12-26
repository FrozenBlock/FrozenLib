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

package net.frozenblock.lib.testmod;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;

public class FrozenTestPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
		ConfigRegistry.register(FrozenLibConfig.INSTANCE, new ConfigModification<>(config -> {
			FrozenLibConfig.SAVE_ITEM_COOLDOWNS.setSyncedValue(true);
			FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS_ENTRY.setSyncedValue(true);
			FrozenLibConfig.REMOVE_EXPERIMENTAL_WARNING.setSyncedValue(true);
			FrozenLibConfig.WARDEN_SPAWN_TRACKER_COMMAND.setSyncedValue(true);
			config.saveItemCooldowns = true;
			config.useWindOnNonFrozenServers = true;
			config.removeExperimentalWarning = true;
			config.wardenSpawnTrackerCommand = true;
		}));
    }
}
