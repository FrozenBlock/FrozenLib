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

import net.frozenblock.lib.resource_pack.api.ResourceLoaderHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface FrozenLibResourceLoader {

	static FrozenLibResourceLoader get(PackType type) {
		return new FrozenLibResourceLoaderImpl(type);
	}

	void registerReloadListener(Identifier id, PreparableReloadListener listener);

	void addListenerOrdering(Identifier firstListener, Identifier secondListener);

	static boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType) {
		return ResourceLoaderHelper.registerBuiltinPack(id, modId, activationType);
	}

	static boolean registerBuiltinPack(Identifier id, String modId, Component displayName, PackActivationType activationType) {
		return ResourceLoaderHelper.registerBuiltinPack(id, modId, displayName, activationType);
	}
}
