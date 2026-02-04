/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.instance.xjs.XjsFormat;
import net.frozenblock.lib.config.api.instance.xjs.XjsObjectMapper;
import net.frozenblock.lib.config.api.instance.xjs.XjsOps;
import net.frozenblock.lib.config.v2.config.map.XjsMap;
import org.jetbrains.annotations.Nullable;
import xjs.data.JsonObject;
import xjs.data.JsonValue;
import xjs.data.serialization.JsonContext;
import xjs.data.serialization.writer.ValueWriter;

public class ConfigSettings<T> {
	private static final Jankson JANKSON = Jankson.builder().build();
	public static final LoadFunction<JsonElement> JANKSON_LOAD = path -> {
		if (!Files.exists(path)) return new Object2ObjectLinkedOpenHashMap<>();
		return new Object2ObjectLinkedOpenHashMap<String, JsonElement>(JANKSON.fromJson(JANKSON.load(path.toFile()), Map.class));
	};
	public static final LoadFunction<JsonValue> XJS_LOAD = path -> {
		if (!Files.exists(path)) return new Object2ObjectLinkedOpenHashMap<>();
		final JsonObject value = (JsonObject) JsonContext.autoParse(path);
		return new Object2ObjectLinkedOpenHashMap<>(value.toMap(value1 -> value1));
	};
	public static final MapFunction<JsonValue> XJS_MAP = element -> {
		if (element instanceof xjs.data.JsonObject object) {
			return new XjsMap(object);
		}
		return null;
	};

	public static final ConfigSettings<JsonElement> JSON = new ConfigSettings<>(
		"json",
		JanksonOps.INSTANCE,
		janksonSave(false, JsonType.JSON),
		JANKSON_LOAD
	);
	public static final ConfigSettings<JsonElement> JSON5 = new ConfigSettings<JsonElement>(
		"json5",
		JanksonOps.INSTANCE,
		janksonSave(true, JsonType.JSON5),
		JANKSON_LOAD
	);
	public static final ConfigSettings<JsonElement> JSON5_UNQUOTED_KEYS = new ConfigSettings<>(
		"json5",
		JanksonOps.INSTANCE,
		janksonSave(true, JsonType.JSON5_UNQUOTED_KEYS),
		JANKSON_LOAD
	);
	public static final ConfigSettings<JsonElement> JSON5_UNQUOTED_KEYS_NO_ROOT = new ConfigSettings<>(
		"json5",
		JanksonOps.INSTANCE,
		janksonSave(true, JsonType.JSON5_UNQUOTED_KEYS_NO_ROOT),
		JANKSON_LOAD
	);
	public static final ConfigSettings<JsonValue> DJS = new ConfigSettings<>(
		"djs",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.DJS),
		XJS_LOAD,
		XJS_MAP
	);
	public static final ConfigSettings<JsonValue> XJS_JSON = new ConfigSettings<>(
		"json",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.JSON),
		XJS_LOAD,
		XJS_MAP
	);
	public static final ConfigSettings<JsonValue> JSONC = new ConfigSettings<>(
		"jsonc",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.JSONC),
		XJS_LOAD,
		XJS_MAP
	);
	public static final ConfigSettings<JsonValue> HJSON = new ConfigSettings<>(
		"hjson",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.HJSON),
		XJS_LOAD,
		XJS_MAP
	);
	public static final ConfigSettings<JsonValue> TXT = new ConfigSettings<>(
		"txt",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.TXT),
		XJS_LOAD,
		XJS_MAP
	);
	public static final ConfigSettings<JsonValue> UBJSON = new ConfigSettings<JsonValue>(
		"ubjson",
		XjsOps.INSTANCE,
		xjsSave(XjsFormat.UBJSON),
		XJS_LOAD,
		XJS_MAP
	);

	private final String fileExtension;
	private final DynamicOps<T> dynamicOps;
	private final SaveFunction<T> saveFunction;
	private final LoadFunction<T> loadFunction;
	/**
	 * Only required if the json object itself is not an instance of {@link Map}
	 */
	@Nullable
	private final MapFunction<T> mapFunction;

	public ConfigSettings(
		String fileExtension,
		DynamicOps<T> dynamicOps,
		SaveFunction<T> saveFunction,
		LoadFunction<T> loadFunction,
		@Nullable MapFunction<T> mapFunction
	) {
		this.fileExtension = fileExtension;
		this.dynamicOps = dynamicOps;
		this.saveFunction = saveFunction;
		this.loadFunction = loadFunction;
		this.mapFunction = mapFunction;
	}

	public ConfigSettings(
		String fileExtension,
		DynamicOps<T> dynamicOps,
		SaveFunction<T> saveFunction,
		LoadFunction<T> loadFunction
	) {
		this(fileExtension, dynamicOps, saveFunction, loadFunction, null);
	}

	public String fileExtension() {
		return this.fileExtension;
	}

	public DynamicOps<T> dynamicOps() {
		return this.dynamicOps;
	}

	@Nullable
	public MapFunction<T> mapFunction() {
		return this.mapFunction;
	}

	public void save(Path path, Map<String, T> configMap) throws Exception {
		this.saveFunction.save(path, configMap, null);
	}

	public void save(Path path, Map<String, T> configMap, Map<String, String> commentMap) throws Exception {
		this.saveFunction.save(path, configMap, commentMap);
	}

	public Map<String, T> load(Path path) throws Exception {
		return this.loadFunction.load(path);
	}

	@FunctionalInterface
	public interface SaveFunction<T> {
		void save(Path path, Map<String, T> configMap, Map<String, String> commentMap) throws Exception;
	}

	@FunctionalInterface
	public interface LoadFunction<T> {
		Map<String, T> load(Path path) throws Exception;
	}

	@FunctionalInterface
	public interface MapFunction<T> {
		Map<String, T> toMap(T object);
	}

	public static SaveFunction<JsonElement> janksonSave(boolean comments, JsonType type) {
		return (path, configMap, commentMap) -> {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				final blue.endless.jankson.JsonObject jsonObject = (blue.endless.jankson.JsonObject) JANKSON.toJson(configMap);
				// Apply comments if provided
				if (comments && commentMap != null && !commentMap.isEmpty()) {
					applyJanksonComments(jsonObject, commentMap, "");
				}
				writer.write(jsonObject.toJson(type.getGrammar()));
			}
		};
	}

	public static SaveFunction<JsonValue> xjsSave(XjsFormat format) {
		return (path, configMap, commentMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			// Apply comments if provided
			if (commentMap != null && !commentMap.isEmpty()) {
				applyXjsComments(value, commentMap, "");
			}
			try (ValueWriter writer = format.createWriter(path.toFile())) {
				writer.write(value);
			}
		};
	}

	// Helper method to apply comments to Jankson JsonObject
	private static void applyJanksonComments(blue.endless.jankson.JsonElement element, Map<String, String> commentMap, String currentPath) {
		if (element instanceof blue.endless.jankson.JsonObject jsonObject) {
			for (Map.Entry<String, blue.endless.jankson.JsonElement> entry : jsonObject.entrySet()) {
				final String key = entry.getKey();
				final String path = currentPath.isEmpty() ? key : currentPath + "/" + key;
				final blue.endless.jankson.JsonElement value = entry.getValue();

				// Check if this path has a comment
				if (commentMap.containsKey(path)) {
					jsonObject.setComment(key, commentMap.get(path));
				}

				// Recursively apply comments to nested objects
				applyJanksonComments(value, commentMap, path);
			}
		} else if (element instanceof blue.endless.jankson.JsonArray jsonArray) {
			int index = 0;
			for (blue.endless.jankson.JsonElement item : jsonArray) {
				final String path = currentPath + "[" + index + "]";
				applyJanksonComments(item, commentMap, path);
				index++;
			}
		}
	}

	// Helper method to apply comments to XJS JsonValue
	private static void applyXjsComments(JsonValue element, Map<String, String> commentMap, String currentPath) {
		if (element instanceof xjs.data.JsonObject jsonObject) {
			for (xjs.data.JsonObject.Member member : jsonObject) {
				final String key = member.getKey();
				final String path = currentPath.isEmpty() ? key : currentPath + "/" + key;
				final JsonValue value = member.getOnly();

				// Check if this path has a comment
				if (commentMap.containsKey(path)) {
					value.setComment(commentMap.get(path));
				}

				// Recursively apply comments to nested objects
				applyXjsComments(value, commentMap, path);
			}
		} else if (element instanceof xjs.data.JsonArray jsonArray) {
			int index = 0;
			for (JsonValue item : jsonArray) {
				final String path = currentPath + "[" + index + "]";
				applyXjsComments(item, commentMap, path);
				index++;
			}
		}
	}
}
