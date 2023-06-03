package net.frozenblock.lib.core.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.frozenblock.lib.FrozenMain;
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
				return FrozenMain.id("notify_reloads");
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				markReloaded();
			}
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (markedReloaded()) {
				OptimizedBiomeTagConditionSource.optimizeAll(server.registryAccess().registryOrThrow(Registries.BIOME));
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
