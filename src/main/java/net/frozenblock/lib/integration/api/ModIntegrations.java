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

package net.frozenblock.lib.integration.api;

import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class ModIntegrations {

    /**
     * Registers a mod integration class
     *
     * @param integration   The mod integration class to register
     * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
     * @return A {@link ModIntegrationSupplier}.
     */
    public static ModIntegrationSupplier<? extends ModIntegration> register(Supplier<? extends ModIntegration> integration, String srcModID, String modID) {
        return Registry.register(FrozenRegistry.MOD_INTEGRATION, ResourceLocation.fromNamespaceAndPath(srcModID, modID), new ModIntegrationSupplier<>(integration, modID));
    }

	/**
	 * Registers a mod integration class
	 *
	 * @param integration   The mod integration class to register
	 * @param unloadedIntegration   The integration to use when the mod is unloaded
	 * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
	 * @return A {@link ModIntegrationSupplier}.
	 */
	public static <T extends ModIntegration> ModIntegrationSupplier<T> register(Supplier<T> integration, Supplier<T> unloadedIntegration, String srcModID, String modID) {
		return Registry.register(FrozenRegistry.MOD_INTEGRATION, ResourceLocation.fromNamespaceAndPath(srcModID, modID), new ModIntegrationSupplier<>(integration, unloadedIntegration, modID));
	}

    public static List<ModIntegrationSupplier<?>> getIntegrationSuppliers() {
        return FrozenRegistry.MOD_INTEGRATION.stream().toList();
    }

	/**
	 * Runs prior to registries freezing in order to allow for the registering of things.
	 */
	public static void initializePreFreeze() {
		for (var integration : FrozenRegistry.MOD_INTEGRATION) {
			integration.getIntegration().initPreFreeze();
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				integration.getIntegration().clientInitPreFreeze();
			}
		}
	}

    /**
     * Initialize all mod integrations.
     */
    public static void initialize() {
        for (var integration : FrozenRegistry.MOD_INTEGRATION) {
            integration.getIntegration().init();
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				integration.getIntegration().clientInit();
			}
        }
    }

}
