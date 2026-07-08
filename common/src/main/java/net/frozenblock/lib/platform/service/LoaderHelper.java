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

package net.frozenblock.lib.platform.service;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.Env;
import org.jetbrains.annotations.Nullable;

public interface LoaderHelper {
	boolean isDevelopmentEnvironment();

	Path getGameDir();

	Path getConfigDir();

	boolean isModLoaded(String modId);

	boolean isFabric();

	boolean isNeoForge();

	<T> @Nullable T ifFabric(Supplier<T> supplier);

	<T> @Nullable T ifNeoForge(Supplier<T> supplier);

	boolean isClient();

	boolean isServer();

	Env getEnv();

	Object getGameObject();

	String[] getLaunchArgs();

	/**
	 * Returns metadata for every loaded mod.
	 * On Fabric, custom data is read from {@code fabric.mod.json}'s {@code custom} block.
	 * On NeoForge, custom data is unavailable ({@link ModEntry#getCustomData} returns empty).
	 */
	List<ModEntry> getAllMods();

	/**
	 * Returns the {@link ModEntry} for the mod with the given id, or empty if not loaded.
	 */
	default Optional<ModEntry> getModById(String modId) {
		return getAllMods().stream().filter(e -> e.getId().equals(modId)).findFirst();
	}

	interface ModEntry {
		String getId();

		String getName();

		/**
		 * Returns the custom data value for the given top-level key, if present.
		 * The value is returned as a {@link JsonElement} regardless of the underlying format.
		 */
		Optional<JsonElement> getCustomData(String key);

		/**
		 * Searches for a resource at the given path within the mod's file (jar or directory).
		 * Equivalent to Fabric's {@code ModContainer.findPath()}.
		 * <p>
		 * The returned {@link Path} may refer to a path inside a ZIP filesystem and must not be
		 * used after the game closes.
		 */
		Optional<Path> findPath(String file);

		/**
		 * Returns all root paths of the mod's content, from which resource sub-paths can be resolved.
		 * Equivalent to Fabric's {@code ModContainer.getRootPaths()}.
		 */
		Collection<Path> getRootPaths();
	}
}
