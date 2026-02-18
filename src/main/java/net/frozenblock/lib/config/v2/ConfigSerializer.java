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

package net.frozenblock.lib.config.v2;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.v2.config.ConfigData;
import net.frozenblock.lib.config.v2.config.ConfigSettings;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import org.jetbrains.annotations.Nullable;

public class ConfigSerializer {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

	public static <T> void saveConfig(ConfigData<T> data) {
		final ID configId = data.id();
		final List<ConfigEntry<?>> entries = List.copyOf(data.entries().values());
		if (entries.isEmpty()) {
			FrozenLibLogUtils.logError("No config entries found for " + configId);
			return;
		}

		final SerializationContext<T> context = SerializationContext.createForSaving(configId, entries);
		try {
			context.saveConfig();
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error saving config " + configId, e);
		}
	}

	public static void saveConfigs(boolean collectAll) {
		final Map<ID, List<ConfigEntry<?>>> configsToSave = collectAll ? collectConfigs() : collectUnsavedConfigs();

		for (Map.Entry<ID, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final ID configId = entry.getKey();
			final ConfigData<?> data = ConfigV2Registry.getData(configId);
			if (data == null) {
				FrozenLibLogUtils.logError("No config data found for " + configId);
				continue;
			}

			saveConfigInternal(data, entry.getValue());
		}
	}

	private static <T> void saveConfigInternal(ConfigData<T> data, List<ConfigEntry<?>> entries) {
		final SerializationContext<T> context = SerializationContext.createForSaving(data.id(), entries);
		try {
			context.saveConfig();
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error saving config " + data.id(), e);
		}
	}

	public static <T> Either<Map<String, T>, String> loadConfigAsMap(ID configId) {
		try {
			final ConfigData<?> data = ConfigV2Registry.getData(configId);
			if (data == null) return Either.right("No config data found for " + configId);

			final Optional<? extends SerializationContext<?>> optionalContext = SerializationContext.createForLoading(data);
			if (optionalContext.isEmpty()) return Either.right("Config file does not exist: " + configId);

			final SerializationContext<?> context = optionalContext.get();
			final Map<String, ?> configMap = context.configMap();
			return Either.left((Map<String, T>) configMap);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error loading config " + configId, e);
			return Either.right("Error loading config: " + e.getMessage());
		}
	}

	public static <T> Map<ID, T> convertToOptimizedConfigMap(ConfigData<T> data, Map<String, T> configMap) {
		final ID configId = data.id();
		final List<ConfigEntry<?>> entries = collectConfigs().get(configId);
		if (entries == null || entries.isEmpty()) {
			FrozenLibLogUtils.logError("No config entries found for " + configId);
			return Map.of();
		}

		final String configIdString = configId.toString();
		final SerializationContext<?> context = SerializationContext.createFromLoadedData(data, configMap);
		final Map<ID, T> optimizedMap = new Object2ObjectLinkedOpenHashMap<>();

		for (ConfigEntry<?> entry : entries) {
			final Optional<T> optionalValue = (Optional<T>) findOrBuildEntry(configIdString, entry, context);
			optionalValue.ifPresent(value -> optimizedMap.put(entry.id(), value));
		}

		return optimizedMap;
	}

	public static <T> Map<String, T> buildConfigMapForSaving(ID configId, List<ConfigEntry<?>> entries, SerializationContext<T> context) {
		final String configIdString = configId.toString();
		for (ConfigEntry<?> entry : entries) {
			findOrBuildEntry(configIdString, entry, context);
		}
		return context.configMap();
	}

	public static <T, V> Either<V, String> getFromUnoptimizedDataMap(ConfigData<T> data, ConfigEntry<V> entry, Map<String, T> configMap) {
		final SerializationContext<T> context = SerializationContext.createFromLoadedData(data, configMap);
		final Optional<V> result = findOrBuildEntry(data.id().toString(), entry, context);

		return result.<Either<V, String>>map(Either::left)
			.orElseGet(() -> Either.right("Entry not found: " + entry.id()));
	}

	public static <T, V> Optional<V> findOrBuildEntry(String configId, ConfigEntry<V> entry, SerializationContext<T> context) {
		final String entryId = entry.id().toString().replace(configId + "/", "");
		final List<String> paths = Arrays.asList(entryId.split("/"));
		final int length = paths.size();

		if (configId.equals(entryId) || length == 0) {
			context.logNoPathError(entryId);
			return Optional.empty();
		}

		Map<String, T> entryMap = context.configMap();
		for (int i = 1; i <= length; i++) {
			final String pathSegment = paths.get(i - 1);

			if (i == length) {
				// Last segment - encode or parse the actual value
				return handleFinalSegment(entryId, pathSegment, entry, entryMap, context);
			} else {
				// Intermediate segment - navigate or create nested map
				entryMap = navigateOrCreateNestedMap(entryId, pathSegment, entryMap, context);
				if (entryMap == null) return Optional.empty();
			}
		}

		return Optional.empty();
	}

	private static <T, V> Optional<V> handleFinalSegment(
		String entryId,
		String pathSegment,
		ConfigEntry<V> entry,
		Map<String, T> entryMap,
		SerializationContext<T> context
	) {
		final DataResult<V> result = context.encodeOrParse(entry, () -> entryMap.get(pathSegment));
		if (result.isError()) {
			context.logUnableToUseError(entryId);
			return Optional.empty();
		}

		final Optional<V> finalResult = result.resultOrPartial();
		if (finalResult.isEmpty()) return Optional.empty();
		if (context.isForLoading()) return finalResult;

		entryMap.put(pathSegment, (T) finalResult.get());

		if (context.isForSaving() && entry.hasComment() && !context.useCommentWrapper()) {
			entry.comment().ifPresent(comment -> context.commentMap().put(entryId, comment));
		}

		return Optional.empty(); // In saving mode, we don't return the value
	}

	@Nullable
	private static <T> Map<String, T> navigateOrCreateNestedMap(
		String entryId,
		String pathSegment,
		Map<String, T> entryMap,
		SerializationContext<T> context
	) {
		final T existing = entryMap.get(pathSegment);
		if (existing instanceof Map) return (Map<String, T>) existing;

		if (context.isForSaving()) {
			// Create new nested map for saving
			final Map<String, T> newMap = new Object2ObjectLinkedOpenHashMap<>();
			entryMap.put(pathSegment, (T) newMap);
			return newMap;
		}

		final var mapFunction = context.settings().mapFunction();
		if (mapFunction != null) return mapFunction.toMap(existing);

		// Loading mode - map not found
		FrozenLibLogUtils.logError("Could not find entry " + entryId, FrozenLibLogUtils.UNSTABLE_LOGGING);
		return null;
	}

	public static Map<ID, List<ConfigEntry<?>>> collectUnsavedConfigs() {
		final List<ID> unsavedConfigIds = new ArrayList<>();
		ConfigV2Registry.allConfigEntries().forEach(entry -> {
			if (entry.isSaved()) return;
			final ID configId = entry.configData().id();
			if (!unsavedConfigIds.contains(configId)) unsavedConfigIds.add(configId);
		});

		final Map<ID, List<ConfigEntry<?>>> configsAndEntries = collectConfigs();
		configsAndEntries.keySet().removeIf(id -> !unsavedConfigIds.contains(id));

		return configsAndEntries;
	}

	public static Map<ID, List<ConfigEntry<?>>> collectConfigs() {
		final Map<ID, List<ConfigEntry<?>>> configsAndEntries = new Object2ObjectLinkedOpenHashMap<>();
		ConfigV2Registry.allConfigEntries().forEach(entry -> {
			final ID configId = entry.configData().id();
			configsAndEntries.computeIfAbsent(configId, _ -> new ArrayList<>()).add(entry);
		});
		return configsAndEntries;
	}

	public record SerializationContext<T>(
		ConfigData<T> configData,
		boolean isForSaving,
		Path path,
		Map<String, T> configMap,
		Map<String, String> commentMap
	) {
		public static <T> SerializationContext<T> createForSaving(ID configId, List<ConfigEntry<?>> entries) {
			final ConfigData<T> data = (ConfigData<T>) ConfigV2Registry.getData(configId);
			if (data == null) throw new IllegalStateException("No config data found for " + configId);

			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + data.settings().fileExtension());
			final Map<String, T> emptyMap = new Object2ObjectLinkedOpenHashMap<>();
			final Map<String, String> commentMap = new Object2ObjectLinkedOpenHashMap<>();
			final SerializationContext<T> saveContext = new SerializationContext<>(data, true, path, emptyMap, commentMap);

			final Map<String, T> configMap = buildConfigMapForSaving(configId, entries, saveContext);
			return new SerializationContext<>(data, true, path, configMap, commentMap);
		}

		public static <T> Optional<SerializationContext<T>> createForLoading(ConfigData<T> data) throws Exception {
			final Path path = CONFIG_PATH.resolve(data.id().toString().replace(':', '/') + "." + data.settings().fileExtension());
			if (!Files.exists(path)) return Optional.empty();

			final Map<String, T> configMap = data.settings().load(path);
			if (configMap.isEmpty()) throw new IllegalStateException("Loaded config map is empty for " + data.id());

			final SerializationContext<T> loadContext = new SerializationContext<>(
				data,
				false,
				path,
				configMap,
				new Object2ObjectLinkedOpenHashMap<>()
			);

			return Optional.of(loadContext);
		}

		public static <T> SerializationContext<T> createFromLoadedData(ConfigData<T> data, Map<String, T> configMap) {
			final Path path = CONFIG_PATH.resolve(data.id().toString().replace(':', '/') + "." + data.settings().fileExtension());
			return new SerializationContext<>(
				data,
				false,
				path,
				configMap,
				new Object2ObjectLinkedOpenHashMap<>()
			);
		}

		public void logNoPathError(String entry) {
			FrozenLibLogUtils.logError(
				"Config entry " + entry + " has no field name to " + (this.isForSaving() ? "save to" : "read from") + "!\nSeparate config ids from fields using '/'."
			);
		}

		public void logUnableToUseError(String entry) {
			FrozenLibLogUtils.logError(
				"Unable to " + (this.isForSaving() ? "save" : "read") + " config entry " + entry
			);
		}

		public boolean useCommentWrapper() {
			return "json".equals(this.fileExtension());
		}

		public ConfigSettings<T> settings() {
			return this.configData.settings();
		}

		public String fileExtension() {
			return this.settings().fileExtension();
		}

		public boolean isForLoading() {
			return !this.isForSaving;
		}

		public <V> DataResult<V> encodeOrParse(ConfigEntry<V> entry, Supplier<T> parseInput) {
			final Codec<V> codec = entry.codec();

			if (this.isForSaving()) {
				// Encode the value
				final DataResult<T> encodedResult = (DataResult<T>) codec.encodeStart(JavaOps.INSTANCE, entry.getActual());
				if (encodedResult.isError()) return (DataResult<V>) encodedResult;

				final Optional<T> encodedValue = encodedResult.resultOrPartial();
				if (encodedValue.isEmpty()) return (DataResult<V>) encodedResult;

				// Handle comments for plain JSON files using wrapper
				if (entry.hasComment() && this.useCommentWrapper()) {
					final Map<String, T> valueWithCommentMap = new Object2ObjectLinkedOpenHashMap<>();
					entry.comment().ifPresent(comment -> valueWithCommentMap.put("comment", (T) comment));
					valueWithCommentMap.put("value", encodedValue.get());
					return DataResult.success((V) valueWithCommentMap);
				}

				// Return the encoded value as-is (comments will be applied during save)
				return DataResult.success((V) encodedValue.get());
			}

			final DynamicOps<T> dynamicOps = this.settings().dynamicOps();
			final T input = parseInput.get();
			final DataResult<V> result = codec.parse(dynamicOps, input);

			if (!result.isError() || !(input instanceof Map<?, ?> map)) return result;

			// Try unwrapping comment wrapper
			final Object value = map.get("value");
			if (value != null) {
				final DataResult<V> valueWithCommentResult = codec.parse(dynamicOps, (T) value);
				if (!valueWithCommentResult.isError()) return valueWithCommentResult;
			}

			return result;
		}

		public void saveConfig() throws Exception {
			if (this.isForLoading()) throw new IllegalStateException("Cannot save config from loading context!");

			final Map<String, T> configMapToSave = this.configMap();
			if (configMapToSave == null || configMapToSave.isEmpty()) return;

			Files.createDirectories(this.path.getParent());
			this.settings().save(this.path, configMapToSave, this.commentMap);
		}
	}
}
