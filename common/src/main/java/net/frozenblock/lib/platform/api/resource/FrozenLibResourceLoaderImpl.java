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
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

record FrozenLibResourceLoaderImpl(PackType type) implements FrozenLibResourceLoader {
	@Override
	public void registerReloadListener(Identifier id, PreparableReloadListener listener) {
		FrozenLibInitPlatformUtils.RESOURCE_LOADER.registerReloadListener(this.type, id, listener);
	}

	@Override
	public void addListenerOrdering(Identifier firstListener, Identifier secondListener) {
		FrozenLibInitPlatformUtils.RESOURCE_LOADER.addListenerOrdering(this.type, firstListener, secondListener);
	}
}
