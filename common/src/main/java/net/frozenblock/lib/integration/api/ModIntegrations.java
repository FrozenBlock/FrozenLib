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

package net.frozenblock.lib.integration.api;

import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;

@UtilityClass
public class ModIntegrations {

    public static List<ModIntegrationSupplier<?>> getIntegrationSuppliers() {
        return FrozenLibBuiltInRegistries.MOD_INTEGRATION.stream().toList();
    }

	/**
	 * Runs prior to registries freezing in order to allow for the registering of things.
	 */
	public static void initializePreFreeze() {
		for (ModIntegrationSupplier<?> integration : FrozenLibBuiltInRegistries.MOD_INTEGRATION) {
			integration.getIntegration().initPreFreeze();
			if (ModLoader.isServer()) continue;

			integration.getIntegration().clientInitPreFreeze();
		}
	}

    /**
     * Initialize all mod integrations.
     */
    public static void initialize() {
        for (ModIntegrationSupplier<?> integration : FrozenLibBuiltInRegistries.MOD_INTEGRATION) {
            integration.getIntegration().init();
			if (ModLoader.isServer()) continue;

			integration.getIntegration().clientInit();
        }
    }
}
