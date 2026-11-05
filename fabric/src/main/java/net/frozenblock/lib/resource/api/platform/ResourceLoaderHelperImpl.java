/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.resource.api.platform;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

public final class ResourceLoaderHelperImpl {
	private static final Map<String, PackSource> PACK_SOURCE_OVERRIDES = new Object2ObjectOpenHashMap<>();

	public static void registerReloadListener(PackType packType, Identifier id, PreparableReloadListener listener) {
		ResourceLoader.get(packType).registerReloadListener(id, listener);
	}

	public static void addListenerOrdering(PackType packType, Identifier firstListener, Identifier secondListener) {
		ResourceLoader.get(packType).addListenerOrdering(firstListener, secondListener);
	}

	public static boolean registerPack(
		@Nullable PackType packType,
		Identifier id,
		String modId,
		Component displayName,
		PackSource source,
		PackActivationType activationType
	) {
		if (packType == null) {
			final boolean registerClientPack = registerPack(PackType.CLIENT_RESOURCES, id, modId, displayName, source, activationType);
			final boolean registerServerPack = registerPack(PackType.SERVER_DATA, id, modId, displayName, source, activationType);
			return registerClientPack || registerServerPack;
		}

		if (source != PackSource.BUILT_IN) PACK_SOURCE_OVERRIDES.put(id.toString(), source);

		final String path;
		if (packType == PackType.CLIENT_RESOURCES) {
			path = "resourcepacks/" + id.getPath();
		} else if (packType == PackType.SERVER_DATA) {
			path = "datapacks/" + id.getPath();
		} else {
			path = null;
		}

		final ModContainer modContainer = getModContainer(modId);
		final net.fabricmc.fabric.api.resource.v1.pack.PackActivationType fabricActivationType = toFabric(activationType);
		return StringUtil.isNullOrEmpty(path)
			? ResourceLoader.registerBuiltinPack(id, modContainer, displayName, fabricActivationType)
			: ResourceLoader.registerBuiltinPack(id, path, modContainer, displayName, fabricActivationType);
	}

	public static Optional<PackSource> getPackSourceOverride(String packId) {
		return Optional.ofNullable(PACK_SOURCE_OVERRIDES.get(packId));
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
