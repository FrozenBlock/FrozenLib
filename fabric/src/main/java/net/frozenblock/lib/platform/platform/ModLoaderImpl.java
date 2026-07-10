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

package net.frozenblock.lib.platform.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.platform.api.Env;
import org.jspecify.annotations.Nullable;

public class ModLoaderImpl {

	public static boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static Path getGameDir() {
		return FabricLoader.getInstance().getGameDir();
	}

	public static Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isFabric() {
		return true;
	}

	public static boolean isNeoForge() {
		return false;
	}

	public static @Nullable <T> T ifFabric(Supplier<T> supplier) {
		return supplier.get();
	}

	public static @Nullable <T> T ifNeoForge(Supplier<T> supplier) {
		return null;
	}

	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static boolean isServer() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
	}

	public static Env getEnv() {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> Env.CLIENT;
			case SERVER -> Env.SERVER;
		};
	}

	public static String[] getLaunchArgs() {
		return FabricLoader.getInstance().getLaunchArguments(true);
	}

	public static List<ModLoader.ModEntry> getAllMods() {
		List<ModLoader.ModEntry> result = new ArrayList<>();
		for (var container : FabricLoader.getInstance().getAllMods()) {
			var metadata = container.getMetadata();
			result.add(new ModLoader.ModEntry() {
				@Override
				public String getId() {
					return metadata.getId();
				}

				@Override
				public String getName() {
					return metadata.getName();
				}

				@Override
				public Optional<JsonElement> getCustomData(String key) {
					CustomValue value = metadata.getCustomValue(key);
					if (value == null) return Optional.empty();
					return Optional.of(toJsonElement(value));
				}

				@Override
				public Optional<Path> findPath(String file) {
					return container.findPath(file);
				}

				@Override
				public Collection<Path> getRootPaths() {
					return container.getRootPaths();
				}
			});
		}
		return result;
	}

	private static JsonElement toJsonElement(CustomValue value) {
		return switch (value.getType()) {
			case OBJECT -> {
				JsonObject obj = new JsonObject();
				for (Map.Entry<String, CustomValue> entry : value.getAsObject()) {
					obj.add(entry.getKey(), toJsonElement(entry.getValue()));
				}
				yield obj;
			}
			case ARRAY -> {
				JsonArray arr = new JsonArray();
				for (CustomValue element : value.getAsArray()) {
					arr.add(toJsonElement(element));
				}
				yield arr;
			}
			case STRING -> new JsonPrimitive(value.getAsString());
			case NUMBER -> new JsonPrimitive(value.getAsNumber());
			case BOOLEAN -> new JsonPrimitive(value.getAsBoolean());
			case NULL -> JsonNull.INSTANCE;
		};
	}
}
