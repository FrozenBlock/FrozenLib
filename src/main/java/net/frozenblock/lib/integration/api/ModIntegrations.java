/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.integration.api;

import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class ModIntegrations {

    private ModIntegrations() {
        throw new UnsupportedOperationException("ModIntegrations contains only static declarations.");
    }

    /**
     * Registers a mod integration class
     *
     * @param integration   The mod integration class to register
     * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
     * @return A {@link ModIntegrationSupplier}.
     */
    public static ModIntegrationSupplier<? extends ModIntegration> register(Supplier<? extends ModIntegration> integration, String srcModID, String modID) {
        return Registry.register(FrozenRegistry.MOD_INTEGRATION, new ResourceLocation(srcModID, modID), new ModIntegrationSupplier<>(integration, modID));
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
		return Registry.register(FrozenRegistry.MOD_INTEGRATION, new ResourceLocation(srcModID, modID), new ModIntegrationSupplier<>(integration, unloadedIntegration, modID));
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
