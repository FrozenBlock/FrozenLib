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

package net.frozenblock.lib.entrypoint.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class FrozenLibEntrypoints {
	public static final String METADATA_FILE = "frozenlib.json";
	private static final Map<String, List<DeclaredEntrypoint>> DECLARED = new HashMap<>();
	private static boolean collected = false;
	private static boolean injectedIntoNativeLoader = false;

	public static synchronized void collect() {
		if (collected) return;
		collected = true;

		for (ModLoader.ModEntry mod : ModLoader.getAllMods()) {
			mod.findPath(METADATA_FILE).ifPresent(path -> readInto(mod.getId(), path));
		}
	}

	private static void readInto(String modId, Path path) {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			mergeFrom(modId, reader);
		} catch (IOException | RuntimeException e) {
			FrozenLibConstants.LOGGER.error("Failed to parse {} for mod {}", METADATA_FILE, modId, e);
		}
	}

	@ApiStatus.Internal
	public static synchronized void collectFromStream(String modId, @Nullable InputStream stream) {
		if (stream == null) return;
		try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
			mergeFrom(modId, reader);
		} catch (IOException | RuntimeException e) {
			FrozenLibConstants.LOGGER.error("Failed to parse {} for mod {}", METADATA_FILE, modId, e);
		}
	}

	@ApiStatus.Internal
	public static synchronized void markCollected() {
		collected = true;
	}

	private static void mergeFrom(String modId, Reader reader) {
		JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
		JsonElement entrypoints = root.get("entrypoints");
		if (entrypoints == null || !entrypoints.isJsonObject()) return;

		for (Map.Entry<String, JsonElement> entry : entrypoints.getAsJsonObject().entrySet()) {
			List<DeclaredEntrypoint> declared = DECLARED.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());
			JsonElement value = entry.getValue();
			if (value.isJsonArray()) {
				for (JsonElement element : value.getAsJsonArray()) declared.add(new DeclaredEntrypoint(modId, element.getAsString()));
			} else {
				declared.add(new DeclaredEntrypoint(modId, value.getAsString()));
			}
		}
	}

	public static Set<String> getKeys() {
		collect();
		return Set.copyOf(DECLARED.keySet());
	}

	public static List<DeclaredEntrypoint> getDeclared(String key) {
		collect();
		return DECLARED.getOrDefault(key, List.of());
	}

	public static <T> void forEachDeclaredEntrypoint(String key, Class<T> type, Consumer<T> consumer) {
		for (DeclaredEntrypoint declared : getDeclared(key)) {
			String className = declared.className();
			try {
				Class<?> clazz = Class.forName(className, true, FrozenLibEntrypoints.class.getClassLoader());
				if (!type.isAssignableFrom(clazz)) {
					FrozenLibConstants.LOGGER.error("Entrypoint '{}' declared for key '{}' does not implement {}", className, key, type.getName());
					continue;
				}
				consumer.accept(type.cast(clazz.getDeclaredConstructor().newInstance()));
			} catch (ReflectiveOperationException e) {
				FrozenLibConstants.LOGGER.error("Failed to load entrypoint '{}' declared for key '{}'", className, key, e);
			}
		}
	}

	@ApiStatus.Internal
	public static void markInjectedIntoNativeLoader() {
		injectedIntoNativeLoader = true;
	}

	@ApiStatus.Internal
	public static boolean isInjectedIntoNativeLoader() {
		return injectedIntoNativeLoader;
	}

	public record DeclaredEntrypoint(String modId, String className) {}

	private FrozenLibEntrypoints() {}
}
