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

package net.frozenblock.lib.config.newconfig.serialize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.instance.ConfigSettings;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.resources.Identifier;

public class ConfigSaver {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

	public static void saveConfigs() throws Exception {
		final Map<Identifier, List<ConfigEntry<?>>> configsToSave = collectUnsavedConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final SerializationContext<?> context = SerializationContext.createForSaving(entry.getKey(), entry.getValue());
			context.saveConfig();
		}
	}

	public static void loadConfigs() throws Exception {
		final Map<Identifier, List<ConfigEntry<?>>> configsToLoad = collectConfigs();
		final Map<Identifier, SerializationContext<?>> configMaps = new Object2ObjectLinkedOpenHashMap<>();

		for (Identifier configId : configsToLoad.keySet()) {
			final ConfigSettings<?> settings = FrozenLibRegistries.CONFIG_SETTINGS.get(configId).orElseThrow().value();
			final SerializationContext<?> context = SerializationContext.createForLoading(configId, settings);


			context.graduateWithMap(configMap);
		}

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToLoad.entrySet()) {
			final Identifier configId = entry.getKey();
			if (!configMaps.containsKey(configId)) continue;

			final Map<String, Object> configMap = configMaps.get(configId);
			for (ConfigEntry configEntry : entry.getValue()) {
				Optional optionalValue = readFromConfigMap(configId, configEntry, configMap);
				if (optionalValue.isPresent()) configEntry.setValue(optionalValue.get(), false);
			}
		}
	}

	public static Map<String, Object> buildOrReadFromConfigMap(Identifier configId, List<ConfigEntry<?>> entries, SerializationContext<?> context) {
		final String configIdString = configId.toString();
		final Map<String, Object> organizedEntries = new Object2ObjectLinkedOpenHashMap<>();

		for (ConfigEntry<?> entry : entries) {
			final String entryId = entry.getId().toString().replace(configIdString + "/", "");
			final List<String> paths = Arrays.stream(entryId.split("/")).toList();
			final int length = paths.size();

			if (configIdString.equals(entryId) || length <= 0) {
				context.logNoPathError(entryId);
				continue;
			}

			Map<String, Object> entryMap = organizedEntries;
			for (int i = 1; i <= length; i++) {
				final String string = paths.get(i - 1);
				if (i == length) {
					final Codec valueCodec = entry.getCodec();
					final DataResult encoded = valueCodec.encodeStart(JavaOps.INSTANCE, entry.getValue());
					if (encoded == null || encoded.isError()) {
						context.logUnableToUseError(entryId);
						break;
					}

					final Optional encodedResult = encoded.resultOrPartial();
					if (encodedResult.isEmpty()) break;

					entryMap.put(string, encodedResult.get());
				} else {
					final Map<String, Object> foundMap = (Map<String, Object>) entryMap.getOrDefault(string, new Object2ObjectLinkedOpenHashMap<>());
					entryMap.put(string, foundMap);
					entryMap = foundMap;
				}
			}
		}

		return organizedEntries;
	}

	public static Optional<Object> readFromConfigMap(Identifier configId, ConfigEntry<?> entry, Map<String, Object> configMap) {
		final String configIdString = configId.toString();
		final String entryId = entry.getId().toString().replace(configIdString + "/", "");
		final List<String> paths = Arrays.stream(entryId.split("/")).toList();
		final int length = paths.size();

		if (configIdString.equals(entryId) || length <= 0) {
			ENTRY_HAS_NO_PATH_ON_SAVE_ERROR.accept(entryId);
			return Optional.empty();
		}

		Map<String, Object> entryMap = configMap;
		for (int i = 1; i <= length; i++) {
			final String string = paths.get(i - 1);
			if (i == length) {
				final Codec valueCodec = entry.getCodec();
				System.out.println(entryMap.get(string));
				final DataResult decoded = valueCodec.parse(JanksonOps.INSTANCE, entryMap.get(string));
				if (decoded == null || decoded.isError()) {
					FrozenLibLogUtils.logError("Unable to load config entry " + entryId + "!");
					break;
				}

				final Optional decodedResult = decoded.resultOrPartial();
				if (decodedResult.isEmpty()) break;

				return Optional.of(decodedResult.get());
			} else {
				final Map<String, Object> foundMap = (Map<String, Object>) entryMap.get(string);
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

	public record SerializationContext<T>(Identifier configId, ConfigSettings<T> settings, boolean save, Path path, AtomicReference<Map<String, Object>> configMap) {
		public static <T> SerializationContext<T> createForSaving(Identifier configId, List<ConfigEntry<?>> entries, ConfigSettings<T> settings) {
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + settings.fileExtension());
			final SerializationContext<T> saveContext = new SerializationContext<>(configId, settings, true, path, new AtomicReference<>());

			final Map<String, Object> configMap = buildOrReadFromConfigMap(configId, entries, saveContext);
			if (!configMap.isEmpty()) saveContext.configMap.set(configMap);

			return saveContext;
		}

		public static <T> SerializationContext<T> createForSaving(Identifier configId, List<ConfigEntry<?>> entries) {
			final ConfigSettings<T> settings = (ConfigSettings<T>) FrozenLibRegistries.CONFIG_SETTINGS.get(configId).orElseThrow().value();
			return createForSaving(configId, entries, settings);
		}

		public static <T> Optional<SerializationContext<T>> createForLoading(Identifier configId, ConfigSettings<T> settings) {
			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + settings.fileExtension());

			if (!Files.exists(path)) return Optional.empty();

			final Map<String, Object> configMap = settings.load(context.path());
			if (configMap.isEmpty()) throw new AssertionError("MAP SHOULDNT BE EMPTY BRUHHHHHHHHHHHHHH");

			return new SerializationContext<>(configId, settings, false, path);
		}

		public void logNoPathError(String entry) {
			FrozenLibLogUtils.logError(
				"Config entry " + entry + " has no field name to" + (save ? "save to" : "read from") + "!\nSeparate config ids from fields using '/'."
			);
		}

		public void logUnableToUseError(String entry) {
			FrozenLibLogUtils.logError(
				"Unable to " + (save ? "save" : "read") + " config entry " + entry + "!"
			);
		}

		public String fileExtension() {
			return this.settings.fileExtension();
		}

		public DynamicOps<T> dynamicOps() {
			return this.settings.dynamicOps();
		}

		public boolean isLoad() {
			return !this.save;
		}

		public File asFile() {
			return this.path.toFile();
		}

		public void saveConfig() throws Exception {
			if (this.isLoad()) throw new IllegalStateException("Cannot save config from loading context!");

			final Map<String, Object> configMap = this.configMap().get();
			if (configMap == null) return;

			Files.createDirectories(this.path.getParent());

			try {
				this.settings.save(this.path, configMap);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			this.settings.save(this.path, configMap);
		}
	}

}
