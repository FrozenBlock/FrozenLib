package net.frozenblock.lib.platform.service;

import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ResourceLoaderHelper {
	void registerReloadListener(PackType packType, Identifier id, PreparableReloadListener listener);

	void addListenerOrdering(PackType packType, Identifier firstListener, Identifier secondListener);

	boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType);

	boolean registerBuiltinPack(Identifier id, String modId, Component displayName, PackActivationType activationType);
}
