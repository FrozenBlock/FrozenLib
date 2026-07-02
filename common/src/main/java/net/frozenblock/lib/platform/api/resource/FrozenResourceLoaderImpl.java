package net.frozenblock.lib.platform.api.resource;

import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

record FrozenResourceLoaderImpl(PackType type) implements FrozenResourceLoader {
	@Override
	public void registerReloadListener(Identifier id, PreparableReloadListener listener) {
		FrozenLibInitPlatformUtils.RESOURCE_LOADER.registerReloadListener(this.type, id, listener);
	}

	@Override
	public void addListenerOrdering(Identifier firstListener, Identifier secondListener) {
		FrozenLibInitPlatformUtils.RESOURCE_LOADER.addListenerOrdering(this.type, firstListener, secondListener);
	}
}
