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

package net.frozenblock.lib.testmod;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;

public class FrozenTestPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
		ConfigRegistry.register(FrozenLibConfig.INSTANCE, new ConfigModification<>(config -> {
			config.saveItemCooldowns = true;
			config.useWindOnNonFrozenServers = true;
			config.removeExperimentalWarning = true;
			config.wardenSpawnTrackerCommand = true;
		}));
    }
}
