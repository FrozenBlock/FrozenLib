package net.frozenblock.lib.platform.api.resource;

import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface FrozenResourceLoader {

	static FrozenResourceLoader get(PackType type) {
		return new FrozenResourceLoaderImpl(type);
	}

	void registerReloadListener(Identifier id, PreparableReloadListener listener);

	void addListenerOrdering(Identifier firstListener, Identifier secondListener);

	static boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType) {
		return FrozenLibInitPlatformUtils.RESOURCE_LOADER.registerBuiltinPack(id, modId, activationType);
	}

	static boolean registerBuiltinPack(Identifier id, String modId, Component displayName, PackActivationType activationType) {
		return FrozenLibInitPlatformUtils.RESOURCE_LOADER.registerBuiltinPack(id, modId, displayName, activationType);
	}
}
