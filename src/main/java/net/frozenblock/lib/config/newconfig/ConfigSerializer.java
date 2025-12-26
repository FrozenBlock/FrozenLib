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

package net.frozenblock.lib.config.newconfig;

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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.config.ConfigSettings;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.resources.Identifier;

public class ConfigSerializer {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

	public static void saveConfigs(boolean collectAll) {
		final Map<Identifier, List<ConfigEntry<?>>> configsToSave = collectAll ? collectConfigs() : collectUnsavedConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final Identifier configId = entry.getKey();
			final SerializationContext<?> context = SerializationContext.createForSaving(configId, entry.getValue());
			try {
				context.saveConfig();
			} catch (Exception e) {
				FrozenLibLogUtils.logError("Error saving config " + configId, e);
			}
		}
	}

	public static Map<String, Object> loadConfigAsMap(Identifier configId) {
		try {
			final Optional<SerializationContext<?>> optionalContext = SerializationContext.createForLoading(configId);
			if (optionalContext.isEmpty()) return Map.of();

			final SerializationContext<?> context = optionalContext.get();
			return Objects.requireNonNull(context.configMap().get());
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error loading config " + configId, e);
		}

		return Map.of();
	}

	public static Map<Identifier, Object> convertToOptimizedConfigMap(ConfigData data, Map<String, Object> configMap) {
		final Identifier configId = data.id();
		final List<ConfigEntry<?>> entries = collectConfigs().get(configId);
		if (entries == null) {
			FrozenLibLogUtils.logError("No config entries found for " + configId);
			return Map.of();
		}

		final String configIdString = configId.toString();
		final SerializationContext context = SerializationContext.createFromLoadedData(data, configMap);
		final Map<Identifier, Object> optimizedMap = new Object2ObjectLinkedOpenHashMap<>();
		for (ConfigEntry entry : entries) {
			Optional optional = findOrBuildEntry(configIdString, entry, context);
			if (optional.isPresent()) optimizedMap.put(entry.getId(), optional.get());
		}

		return optimizedMap;
	}

	public static Map<String, Object> buildConfigMapForSaving(Identifier configId, List<ConfigEntry<?>> entries, SerializationContext<?> context) {
		final String configIdString = configId.toString();
		for (ConfigEntry<?> entry : entries) findOrBuildEntry(configIdString, entry, context);
		return context.configMap().get();
	}

	public static Object getFromUnoptimizedDataMap(ConfigData data, ConfigEntry<?> entry, Map<String, Object> configMap) {
		final SerializationContext<?> context = SerializationContext.createFromLoadedData(data, configMap);
		return findOrBuildEntry(data.id().toString(), entry, context).orElse(null);
	}

	public static Optional<?> findOrBuildEntry(String configId, ConfigEntry<?> entry, SerializationContext<?> context) {
		final String entryId = entry.getId().toString().replace(configId + "/", "");
		final List<String> paths = Arrays.stream(entryId.split("/")).toList();
		final int length = paths.size();

		if (configId.equals(entryId) || length <= 0) {
			context.logNoPathError(entryId);
			return Optional.empty();
		}

		Map<String, Object> entryMap = context.configMap().get();
		for (int i = 1; i <= length; i++) {
			final String string = paths.get(i - 1);
			if (i == length) {
				final Map<String, Object> finalEntryMap = entryMap;
				final DataResult result = context.encodeOrParse(entry, () -> finalEntryMap.get(string));
				if (result == null || result.isError()) {
					context.logUnableToUseError(entryId);
					break;
				}

				final Optional<?> finalResult = result.resultOrPartial();
				if (finalResult.isEmpty()) break;
				if (context.isForLoading()) return finalResult;

				finalEntryMap.put(string, finalResult.get());

				// Track comment if present and not using wrapper
				if (context.isForSaving() && entry.hasComment() && !context.useCommentWrapper()) {
					context.commentMap().put(entryId, entry.getComment().get());
				}
			} else {
				final Map<String, Object> foundMap = (Map<String, Object>) entryMap.getOrDefault(string, context.isForSaving() ? new Object2ObjectLinkedOpenHashMap<>() : null);
				if (foundMap == null) {
					FrozenLibLogUtils.logError("Could not find entry " + entryId, FrozenLibLogUtils.UNSTABLE_LOGGING);
					return Optional.empty();
				}
				entryMap.put(string, foundMap);
				entryMap = foundMap;
			}
		}

		return Optional.empty();
	}

	public static Map<Identifier, List<ConfigEntry<?>>> collectUnsavedConfigs() {
		final List<Identifier> unsavedConfigIds = new ArrayList<>();
		FrozenLibRegistries.CONFIG_ENTRY.forEach(entry -> {
			if (entry.isSaved()) return;
			final Identifier configId = entry.getConfigData().id();
			if (!unsavedConfigIds.contains(configId)) unsavedConfigIds.add(configId);
		});

		final Map<Identifier, List<ConfigEntry<?>>> configsAndEntries = collectConfigs();
		configsAndEntries.keySet().removeIf(id -> !unsavedConfigIds.contains(id));

		return configsAndEntries;
	}

	public static Map<Identifier, List<ConfigEntry<?>>> collectConfigs() {
		final Map<Identifier, List<ConfigEntry<?>>> configsAndEntries = new Object2ObjectLinkedOpenHashMap<>();
		FrozenLibRegistries.CONFIG_ENTRY.forEach(entry -> {
			final Identifier configId = entry.getConfigData().id();
			final List<ConfigEntry<?>> entries = configsAndEntries.getOrDefault(configId, new ArrayList<>());
			entries.add(entry);
			configsAndEntries.put(configId, entries);
		});
		return configsAndEntries;
	}

	public record SerializationContext<T>(ConfigData configData, boolean isForSaving, Path path, AtomicReference<Map<String, Object>> configMap, Map<String, String> commentMap) {
		public static <T> SerializationContext<T> createForSaving(Identifier configId, List<ConfigEntry<?>> entries) {
			final ConfigData<T> data = (ConfigData<T>) FrozenLibRegistries.CONFIG_DATA.get(configId).orElseThrow().value();
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + data.settings().fileExtension());
			final SerializationContext<T> saveContext = new SerializationContext<>(data, true, path, new AtomicReference<>(new Object2ObjectLinkedOpenHashMap<>()), new Object2ObjectLinkedOpenHashMap<>());

			final Map<String, Object> configMap = buildConfigMapForSaving(configId, entries, saveContext);
			saveContext.configMap.set(configMap);

			return saveContext;
		}

		public static Optional<SerializationContext<?>> createForLoading(Identifier configId) throws Exception {
			final ConfigData<?> data = FrozenLibRegistries.CONFIG_DATA.get(configId).orElseThrow().value();
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + data.settings().fileExtension());
			if (!Files.exists(path)) return Optional.empty();

			final Map<String, Object> configMap = data.settings().load(path);
			if (configMap.isEmpty()) throw new AssertionError("MAP SHOULDNT BE EMPTY BRUHHHHHHHHHHHHHH");

			final SerializationContext<?> loadContext = new SerializationContext<>(data, false, path, new AtomicReference<>(configMap), new Object2ObjectLinkedOpenHashMap<>());

			return Optional.of(loadContext);
		}

		public static SerializationContext<?> createFromLoadedData(ConfigData<?> data, Map<String, Object> configMap) {
			final Path path = CONFIG_PATH.resolve(data.id().toString().replace(':', '/') + "." + data.settings().fileExtension());
			return new SerializationContext<>(data, false, path, new AtomicReference<>(configMap), new Object2ObjectLinkedOpenHashMap<>());
		}

		public void logNoPathError(String entry) {
			FrozenLibLogUtils.logError(
				"Config entry " + entry + " has no field name to" + (this.isForSaving() ? "save to" : "read from") + "!\nSeparate config ids from fields using '/'."
			);
		}

		public void logUnableToUseError(String entry) {
			FrozenLibLogUtils.logError(
				"Unable to " + (this.isForSaving() ? "save" : "read") + " config entry " + entry
			);
		}

		private boolean useCommentWrapper() {
			return this.fileExtension().equals("json");
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

		public DataResult<?> encodeOrParse(ConfigEntry entry, Supplier<?> parseInput) {
			final Codec codec = entry.getCodec();

			if (this.isForSaving()) {
				// Encode the value
				final DataResult<?> encodedResult = codec.encodeStart(JavaOps.INSTANCE, entry.getActual());
				if (encodedResult.isError()) return encodedResult;

				final Object encodedValue = encodedResult.resultOrPartial().orElse(null);
				if (encodedValue == null) return encodedResult;

				// Handle comments for plain JSON files using wrapper
				if (entry.hasComment() && this.useCommentWrapper()) {
					final Map<String, Object> valueWithCommentMap = new Object2ObjectLinkedOpenHashMap<>();
					valueWithCommentMap.put("comment", entry.getComment().get());
					valueWithCommentMap.put("value", encodedValue);
					return DataResult.success(valueWithCommentMap);
				}

				// Return the encoded value as-is (comments will be applied during save)
				return encodedResult;
			}

			final DynamicOps<T> dynamicOps = this.settings().dynamicOps();
			final Object input = parseInput.get();
			DataResult result = codec.parse(dynamicOps, input);
			if (!result.isError() || !(input instanceof Map<?,?> map) || !(map.get("value") instanceof Object value)) return result;

			final DataResult valueWithCommentResult = codec.parse(dynamicOps, value);
			if (!valueWithCommentResult.isError()) return valueWithCommentResult;

			return result;
		}

		public void saveConfig() throws Exception {
			if (this.isForLoading()) throw new IllegalStateException("Cannot save config from loading context!");

			final Map<String, Object> configMap = this.configMap().get();
			if (configMap == null) return;

			Files.createDirectories(this.path.getParent());
			this.settings().save(this.path, configMap, this.commentMap);
		}
	}

}
