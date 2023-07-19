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

package net.frozenblock.lib.integration.api.client;

import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.registry.api.client.FrozenClientRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class ClientModIntegrations {

    private ClientModIntegrations() {
        throw new UnsupportedOperationException("ClientModIntegrations contains only static declarations.");
    }

    /**
     * Registers a client mod integration class
     *
     * @param integration   The client mod integration class to register
     * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
     * @return A {@link ClientModIntegrationSupplier}.
     */
    public static ClientModIntegrationSupplier<? extends ClientModIntegration> register(Supplier<? extends ClientModIntegration> integration, String srcModID, String modID) {
        return Registry.register(FrozenClientRegistry.CLIENT_MOD_INTEGRATION, new ResourceLocation(srcModID, modID), new ClientModIntegrationSupplier<>(integration, modID));
    }

	/**
	 * Registers a client mod integration class
	 *
	 * @param integration   The client mod integration class to register
	 * @param unloadedIntegration   The integration to use when the mod is unloaded
	 * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
	 * @return A {@link ClientModIntegrationSupplier}.
	 */
	public static <T extends ClientModIntegration> ClientModIntegrationSupplier<T> register(Supplier<T> integration, Supplier<T> unloadedIntegration, String srcModID, String modID) {
		return Registry.register(FrozenClientRegistry.CLIENT_MOD_INTEGRATION, new ResourceLocation(srcModID, modID), new ClientModIntegrationSupplier<>(integration, unloadedIntegration, modID));
	}

    public static List<ClientModIntegrationSupplier<?>> getIntegrationSuppliers() {
        return FrozenClientRegistry.CLIENT_MOD_INTEGRATION.stream().toList();
    }

    /**
     * Initialize all mod integrations.
     */
    public static void initialize() {
        for (var integration : FrozenClientRegistry.CLIENT_MOD_INTEGRATION) {
            integration.getIntegration().init();
        }
    }

}
