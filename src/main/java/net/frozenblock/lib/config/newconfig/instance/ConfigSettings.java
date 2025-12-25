/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.config.newconfig.instance;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import com.mojang.serialization.DynamicOps;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.instance.xjs.XjsFormat;
import net.frozenblock.lib.config.api.instance.xjs.XjsObjectMapper;
import net.frozenblock.lib.config.api.instance.xjs.XjsOps;
import xjs.data.JsonValue;
import xjs.data.serialization.writer.ValueWriter;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class ConfigSettings<T> {

	private static final Jankson JANKSON = Jankson.builder().build();

	public static final ConfigSettings<JsonElement> JSON = new ConfigSettings<>(
		"json",
		JanksonOps.INSTANCE,
		(path, configMap) -> {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				writer.write(JANKSON.toJson(configMap).toJson(JsonType.JSON.getGrammar()));
			}
		}
	);
	public static final ConfigSettings<JsonElement> JSON5 = new ConfigSettings<>(
		"json5",
		JanksonOps.INSTANCE,
		(path, configMap) -> {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				writer.write(JANKSON.toJson(configMap).toJson(JsonType.JSON5.getGrammar()));
			}
		}
	);
	public static final ConfigSettings<JsonElement> JSON5_UNQUOTED_KEYS = new ConfigSettings<>(
		"json5",
		JanksonOps.INSTANCE,
		(path, configMap) -> {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				writer.write(JANKSON.toJson(configMap).toJson(JsonType.JSON5_UNQUOTED_KEYS.getGrammar()));
			}
		}
	);
	public static final ConfigSettings<JsonValue> DJS = new ConfigSettings<>(
		"djs",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.DJS.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);
	public static final ConfigSettings<JsonValue> XJS_JSON = new ConfigSettings<>(
		"json",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.JSON.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);
	public static final ConfigSettings<JsonValue> JSONC = new ConfigSettings<>(
		"jsonc",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.JSONC.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);
	public static final ConfigSettings<JsonValue> HJSON = new ConfigSettings<>(
		"hjson",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.HJSON.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);
	public static final ConfigSettings<JsonValue> TXT = new ConfigSettings<>(
		"txt",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.TXT.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);
	public static final ConfigSettings<JsonValue> UBJSON = new ConfigSettings<>(
		"ubjson",
		XjsOps.INSTANCE,
		(path, configMap) -> {
			final JsonValue value = XjsObjectMapper.toJsonObject(configMap);
			try (ValueWriter writer = XjsFormat.UBJSON.createWriter(path.toFile())) {
				writer.write(value);
			}
		}
	);

	private final String fileExtension;
	private final DynamicOps<T> dynamicOps;
	private final SaveFunction saveFunction;

	public ConfigSettings(
		String fileExtension,
		DynamicOps<T> dynamicOps,
		SaveFunction saveFunction
	) {
		this.fileExtension = fileExtension;
		this.dynamicOps = dynamicOps;
		this.saveFunction = saveFunction;
	}

	public String fileExtension() {
		return this.fileExtension;
	}

	public DynamicOps<T> dynamicOps() {
		return this.dynamicOps;
	}

	public void save(Path path, Map<String, Object> configMap) throws Exception {
		this.saveFunction.save(path, configMap);
	}

	@FunctionalInterface
	public interface SaveFunction {
		void save(Path path, Map<String, Object> configMap) throws Exception;
	}
}
