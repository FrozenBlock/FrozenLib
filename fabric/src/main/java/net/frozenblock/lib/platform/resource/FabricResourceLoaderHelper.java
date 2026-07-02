package net.frozenblock.lib.platform.resource;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.frozenblock.lib.platform.service.ResourceLoaderHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class FabricResourceLoaderHelper implements ResourceLoaderHelper {

	@Override
	public void registerReloadListener(PackType packType, Identifier id, PreparableReloadListener listener) {
		ResourceLoader.get(packType).registerReloadListener(id, listener);
	}

	@Override
	public void addListenerOrdering(PackType packType, Identifier firstListener, Identifier secondListener) {
		ResourceLoader.get(packType).addListenerOrdering(firstListener, secondListener);
	}

	@Override
	public boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType) {
		return ResourceLoader.registerBuiltinPack(id, getModContainer(modId), toFabric(activationType));
	}

	@Override
	public boolean registerBuiltinPack(Identifier id, String modId, Component displayName, PackActivationType activationType) {
		return ResourceLoader.registerBuiltinPack(id, getModContainer(modId), displayName, toFabric(activationType));
	}

	private static ModContainer getModContainer(String modId) {
		return FabricLoader.getInstance().getModContainer(modId)
			.orElseThrow(() -> new IllegalArgumentException("Mod not found: " + modId));
	}

	private static net.fabricmc.fabric.api.resource.v1.pack.PackActivationType toFabric(PackActivationType activationType) {
		return switch (activationType) {
			case NORMAL -> net.fabricmc.fabric.api.resource.v1.pack.PackActivationType.NORMAL;
			case DEFAULT_ENABLED -> net.fabricmc.fabric.api.resource.v1.pack.PackActivationType.DEFAULT_ENABLED;
			case ALWAYS_ENABLED -> net.fabricmc.fabric.api.resource.v1.pack.PackActivationType.ALWAYS_ENABLED;
		};
	}
}
