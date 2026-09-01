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

package net.frozenblock.lib.entrypoint.impl.platform;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.entrypoint.EntrypointStorage;
import net.fabricmc.loader.impl.metadata.EntrypointMetadata;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.entrypoint.impl.FrozenLibEntrypoints;

public final class FrozenLibEntrypointInjectorImpl {

	public static void inject() {
		EntrypointStorage storage;
		Map<String, LanguageAdapter> adapterMap;
		try {
			final FabricLoaderImpl loader = FabricLoaderImpl.INSTANCE;

			final Field storageField = FabricLoaderImpl.class.getDeclaredField("entrypointStorage");
			storageField.setAccessible(true);
			storage = (EntrypointStorage) storageField.get(loader);

			final Field adapterField = FabricLoaderImpl.class.getDeclaredField("adapterMap");
			adapterField.setAccessible(true);
			@SuppressWarnings("unchecked")
			final Map<String, LanguageAdapter> castAdapterMap = (Map<String, LanguageAdapter>) adapterField.get(loader);
			adapterMap = castAdapterMap;
		} catch (ReflectiveOperationException | ClassCastException e) {
			FrozenLibLogUtils.LOGGER.error(
				"Failed to hook into Fabric Loader's entrypoint storage - frozenlib.json entrypoints will "
					+ "only be visible through EntrypointHelper, not FabricLoader#getEntrypoints",
				e
			);
			return;
		}

		for (String key : FrozenLibEntrypoints.getKeys()) {
			for (FrozenLibEntrypoints.DeclaredEntrypoint declared : FrozenLibEntrypoints.getDeclared(key)) {
				final Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(declared.modId());
				if (container.isEmpty() || !(container.get() instanceof ModContainerImpl modContainer)) continue;

				try {
					storage.add(modContainer, key, new EntrypointMetadata() {
						@Override
						public String getAdapter() {
							return "default";
						}

						@Override
						public String getValue() {
							return declared.className();
						}
					}, adapterMap);
				} catch (Exception e) {
					FrozenLibLogUtils.LOGGER.error(
						"Failed to inject frozenlib.json entrypoint '{}' (key '{}') into Fabric Loader", declared.className(), key, e
					);
				}
			}
		}

		FrozenLibEntrypoints.markInjectedIntoNativeLoader();
	}

	private FrozenLibEntrypointInjectorImpl() {}
}
