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

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.instance.ConfigSettings;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.resources.Identifier;

public class ConfigSerializer {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

	public static void saveConfigs(boolean collectAll) throws Exception {
		final Map<Identifier, List<ConfigEntry<?>>> configsToSave = collectAll ? collectConfigs() : collectUnsavedConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final SerializationContext<?> context = SerializationContext.createForSaving(entry.getKey(), entry.getValue());
			context.saveConfig();
		}
	}

	public static void loadConfigs() throws Exception {
		final Map<Identifier, List<ConfigEntry<?>>> configsToLoad = collectConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToLoad.entrySet()) {
			final Identifier configId = entry.getKey();
			final Optional<SerializationContext<?>> optionalContext = SerializationContext.createForLoading(configId);
			if (optionalContext.isEmpty()) continue;

			final SerializationContext<?> context = optionalContext.get();
			context.loadEntries(entry.getValue());
		}
	}

	public static Map<String, Object> buildConfigMapForSaving(Identifier configId, List<ConfigEntry<?>> entries, SerializationContext<?> context) {
		final String configIdString = configId.toString();
		for (ConfigEntry<?> entry : entries) findOrBuildEntry(configIdString, entry, context);
		return context.configMap().get();
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
				if (context.isLoad()) return finalResult;

				finalEntryMap.put(string, finalResult.get());
			} else {
				final Map<String, Object> foundMap = (Map<String, Object>) entryMap.getOrDefault(string, context.isSave() ? new Object2ObjectLinkedOpenHashMap<>() : null);
				if (foundMap == null) throw new AssertionError("NO MAP FOUND OMG HOG!!!!");
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
			final Identifier configId = getBaseConfigIdFromEntry(entry);
			if (!unsavedConfigIds.contains(configId)) unsavedConfigIds.add(configId);
		});

		final Map<Identifier, List<ConfigEntry<?>>> configsAndEntries = collectConfigs();
		configsAndEntries.keySet().removeIf(id -> !unsavedConfigIds.contains(id));

		return configsAndEntries;
	}

	public static Map<Identifier, List<ConfigEntry<?>>> collectConfigs() {
		final Map<Identifier, List<ConfigEntry<?>>> configsAndEntries = new Object2ObjectLinkedOpenHashMap<>();
		FrozenLibRegistries.CONFIG_ENTRY.forEach(entry -> {
			final Identifier configId = getBaseConfigIdFromEntry(entry);
			final List<ConfigEntry<?>> entries = configsAndEntries.getOrDefault(configId, new ArrayList<>());
			entries.add(entry);
			configsAndEntries.put(configId, entries);
		});
		return configsAndEntries;
	}

	public static Identifier getBaseConfigIdFromEntry(ConfigEntry<?> entry) {
		return entry.getId().withPath(path -> path.split("/")[0]);
	}

	public record SerializationContext<T>(Identifier configId, ConfigSettings<T> settings, boolean isSave, Path path, AtomicReference<Map<String, Object>> configMap) {
		public static <T> SerializationContext<T> createForSaving(Identifier configId, List<ConfigEntry<?>> entries) {
			final ConfigSettings<T> settings = (ConfigSettings<T>) FrozenLibRegistries.CONFIG_SETTINGS.get(configId).orElseThrow().value();
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + settings.fileExtension());
			final SerializationContext<T> saveContext = new SerializationContext<>(configId, settings, true, path, new AtomicReference<>(new Object2ObjectLinkedOpenHashMap<>()));

			final Map<String, Object> configMap = buildConfigMapForSaving(configId, entries, saveContext);
			saveContext.configMap.set(configMap);

			return saveContext;
		}

		public static Optional<SerializationContext<?>> createForLoading(Identifier configId) throws Exception {
			final ConfigSettings<?> settings = FrozenLibRegistries.CONFIG_SETTINGS.get(configId).orElseThrow().value();
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + settings.fileExtension());
			if (!Files.exists(path)) return Optional.empty();

			final Map<String, Object> configMap = settings.load(path);
			if (configMap.isEmpty()) throw new AssertionError("MAP SHOULDNT BE EMPTY BRUHHHHHHHHHHHHHH");

			final SerializationContext<?> loadContext = new SerializationContext<>(configId, settings, false, path, new AtomicReference<>(configMap));

			return Optional.of(loadContext);
		}

		public void logNoPathError(String entry) {
			FrozenLibLogUtils.logError(
				"Config entry " + entry + " has no field name to" + (this.isSave() ? "save to" : "read from") + "!\nSeparate config ids from fields using '/'."
			);
		}

		public void logUnableToUseError(String entry) {
			FrozenLibLogUtils.logError(
				"Unable to " + (this.isSave() ? "save" : "read") + " config entry " + entry + "!"
			);
		}

		public String fileExtension() {
			return this.settings.fileExtension();
		}

		public DynamicOps<T> dynamicOps() {
			return this.settings.dynamicOps();
		}

		public boolean isLoad() {
			return !this.isSave;
		}

		public File asFile() {
			return this.path.toFile();
		}

		public DataResult<?> encodeOrParse(ConfigEntry entry, Supplier<?> parseInput) {
			if (this.isSave()) {
				if (!entry.hasComment()) return entry.getCodec().encodeStart(JavaOps.INSTANCE, entry.getValue());

				final Map<String, Object> valueWithCommentMap = new Object2ObjectLinkedOpenHashMap<>();
				valueWithCommentMap.put("value", entry.getValue());
				valueWithCommentMap.put("comment", entry.getComment().get());
				return DataResult.success(valueWithCommentMap);
			}

			final Object input = parseInput.get();
			DataResult result = entry.getCodec().parse(JanksonOps.INSTANCE, input);
			if (result.isError()) {
				final DataResult<ConfigEntry.ValueWithComment> resultWithComment = entry.getCodecWithComment().parse(JanksonOps.INSTANCE, input);
				if (!resultWithComment.isError()) {
					final Object value = resultWithComment.getOrThrow().value();
					return DataResult.success(value);
				}
			}

			return result;
		}

		public void saveConfig() throws Exception {
			if (this.isLoad()) throw new IllegalStateException("Cannot save config from loading context!");

			final Map<String, Object> configMap = this.configMap().get();
			if (configMap == null) return;

			Files.createDirectories(this.path.getParent());
			this.settings.save(this.path, configMap);
		}

		public void loadEntries(List<ConfigEntry<?>> entries) throws Exception {
			if (this.isSave()) throw new IllegalStateException("Cannot load config entry from saving context!");

			for (ConfigEntry configEntry : entries) {
				final Optional optionalValue = findOrBuildEntry(this.configId.toString(), configEntry, this);
				if (optionalValue.isPresent()) configEntry.setValue(optionalValue.get(), false);
			}
		}
	}

}
