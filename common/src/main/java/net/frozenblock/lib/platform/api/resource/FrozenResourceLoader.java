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
