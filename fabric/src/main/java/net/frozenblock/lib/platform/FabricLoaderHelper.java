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

package net.frozenblock.lib.platform;

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
import net.frozenblock.lib.platform.api.Env;
import net.frozenblock.lib.platform.service.LoaderHelper;
import org.jspecify.annotations.Nullable;

public class FabricLoaderHelper implements LoaderHelper {
	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public Path getGameDir() {
		return FabricLoader.getInstance().getGameDir();
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isFabric() {
		return true;
	}

	@Override
	public boolean isNeoForge() {
		return false;
	}

	@Override
	public @Nullable <T> T ifFabric(Supplier<T> supplier) {
		return supplier.get();
	}

	@Override
	public @Nullable <T> T ifNeoForge(Supplier<T> supplier) {
		return null;
	}

	@Override
	public boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public boolean isServer() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
	}

	@Override
	public Env getEnv() {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> Env.CLIENT;
			case SERVER -> Env.SERVER;
		};
	}

	@Override
	public Object getGameObject() {
		return FabricLoader.getInstance().getGameInstance();
	}

	@Override
	public String[] getLaunchArgs() {
		return FabricLoader.getInstance().getLaunchArguments(true);
	}

	@Override
	public List<ModEntry> getAllMods() {
		List<ModEntry> result = new ArrayList<>();
		for (var container : FabricLoader.getInstance().getAllMods()) {
			var metadata = container.getMetadata();
			result.add(new ModEntry() {
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
