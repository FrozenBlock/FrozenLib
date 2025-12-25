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

import blue.endless.jankson.Jankson;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.instance.ConfigSettings;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.resources.Identifier;

public class ConfigSaver {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
	private static final Jankson JANKSON = Jankson.builder().build();
	private static final Consumer<String> ENTRY_HAS_NO_PATH_ON_SAVE_ERROR = string -> FrozenLibLogUtils.logError(
		"Config entry " + string + " has no field name to save to!\nSeparate config ids from fields using '/'."
	);

	public static void saveConfigs() throws IOException {
		final Map<Identifier, List<ConfigEntry<?>>> configsToSave = collectModifiedConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final Identifier configId = entry.getKey();
			final ConfigSettings<?> settings = FrozenLibRegistries.CONFIG_SETTINGS.get(configId).orElseThrow().value();
			final List<ConfigEntry<?>> configEntries = entry.getValue();


			final Map<String, Object> configMap = buildConfigMapToSave(configId, configEntries);
			if (configMap.isEmpty()) continue;

			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + "." + settings.fileExtension());
			Files.createDirectories(path.getParent());

			try {
				settings.save(path, configMap);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Map<String, Object> buildConfigMapToSave(Identifier configId, List<ConfigEntry<?>> entries) {
		final String configIdString = configId.toString();
		final Map<String, Object> organizedEntries = new Object2ObjectOpenHashMap<>();

		for (ConfigEntry<?> entry : entries) {
			final String entryId = entry.getId().toString().replace(configIdString + "/", "");
			final List<String> paths = Arrays.stream(entryId.split("/")).toList();
			final int length = paths.size();

			if (configIdString.equals(entryId) || length <= 0) {
				ENTRY_HAS_NO_PATH_ON_SAVE_ERROR.accept(entryId);
				continue;
			}

			Map<String, Object> entryMap = organizedEntries;
			for (int i = 1; i <= length; i++) {
				final String string = paths.get(i - 1);
				if (i == length) {
					final Codec valueCodec = entry.getCodec();
					final DataResult encoded = valueCodec.encodeStart(JavaOps.INSTANCE, entry.getValue());
					if (encoded == null || encoded.isError()) {
						FrozenLibLogUtils.logError("Unable to save config entry " + entryId + "!");
						break;
					}

					final Optional encodedResult = encoded.resultOrPartial();
					if (encodedResult.isEmpty()) break;

					entryMap.put(string, encodedResult.get());
				} else {
					final Map<String, Object> foundMap = (Map<String, Object>) entryMap.getOrDefault(string, new Object2ObjectOpenHashMap<>());
					entryMap.put(string, foundMap);
					entryMap = foundMap;
				}
			}
		}

		return organizedEntries;
	}

	public static Map<Identifier, List<ConfigEntry<?>>> collectModifiedConfigs() {
		final List<Identifier> modifiedConfigs = new ArrayList<>();
		FrozenLibRegistries.CONFIG_ENTRY.forEach(entry -> {
			if (entry.isSaved()) return;
			final Identifier configId = getBaseConfigIdFromEntry(entry);
			if (!modifiedConfigs.contains(configId)) modifiedConfigs.add(configId);
		});

		final Map<Identifier, List<ConfigEntry<?>>> configsAndEntries = new Object2ObjectOpenHashMap<>();
		FrozenLibRegistries.CONFIG_ENTRY.forEach(entry -> {
			final Identifier configId = getBaseConfigIdFromEntry(entry);
			final List<ConfigEntry<?>> configs = configsAndEntries.getOrDefault(configId, new ArrayList<>());
			configs.add(entry);
			configsAndEntries.put(configId, configs);
		});

		return configsAndEntries;
	}

	public static Identifier getBaseConfigIdFromEntry(ConfigEntry<?> entry) {
		return entry.getId().withPath(path -> path.split("/")[0]);
	}

}
