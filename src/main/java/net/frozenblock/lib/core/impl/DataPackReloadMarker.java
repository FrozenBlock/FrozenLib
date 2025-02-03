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

package net.frozenblock.lib.core.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.surface.impl.OptimizedBiomeTagConditionSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class DataPackReloadMarker {

	private static boolean RELOADED = false;

	public static void init() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return FrozenLibConstants.id("notify_reloads");
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				markReloaded();
			}
		});

		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			if (markedReloaded()) {
				OptimizedBiomeTagConditionSource.optimizeAll(server.registryAccess().registryOrThrow(Registries.BIOME));
			}
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (markedReloaded()) {
				unmarkReloaded();
			}
		});
	}

	public static void markReloaded() {
		RELOADED = true;
	}

	public static void unmarkReloaded() {
		RELOADED = false;
	}

	public static boolean markedReloaded() {
		return RELOADED;
	}
}
