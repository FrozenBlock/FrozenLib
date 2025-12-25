package net.frozenblock.lib.config.newconfig.serialize;

import blue.endless.jankson.Jankson;
import com.mojang.serialization.Codec;
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
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.resources.Identifier;

public class ConfigSaver {
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
	private static final Jankson JANKSON = Jankson.builder().build();

	public static void saveConfigs() throws IOException {
		final Map<Identifier, List<ConfigEntry<?>>> configsToSave = collectModifiedConfigs();

		for (Map.Entry<Identifier, List<ConfigEntry<?>>> entry : configsToSave.entrySet()) {
			final Identifier configId = entry.getKey();
			final List<ConfigEntry<?>> configEntries = entry.getValue();


			final Map<String, Object> configMap = buildConfigMapToSave(configId, configEntries);
			if (configMap.isEmpty()) continue;

			final Path path = CONFIG_PATH.resolve(configId.toString().replace(':', '/') + ".json");
			Files.createDirectories(path.getParent());

			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				writer.write(JANKSON.toJson(configMap).toJson(JsonType.JSON.getGrammar()));
			}
		}
	}

	public static Map<String, Object> buildConfigMapToSave(Identifier configId, List<ConfigEntry<?>> entries) {
		final String configIdString = configId.toString();
		final Map<String, Object> organizedEntries = new Object2ObjectOpenHashMap<>();

		for (ConfigEntry<?> entry : entries) {
			final String entryId = entry.getId().toString().replace(configIdString + "/", "");

			if (configIdString.equals(entryId)) {
				FrozenLibLogUtils.logError("Config entry " + entryId + " has the same ID as an existing config!");
				continue;
			}

			final List<String> paths = Arrays.stream(entryId.split("/")).toList();
			final int length = paths.size();
			if (length <= 0) {
				FrozenLibLogUtils.logError("Config entry " + entryId + " has no field name to save to!");
				continue;
			}

			Map<String, Object> entryMap = organizedEntries;
			for (int i = 1; i <= length; i++) {
				final String string = paths.get(i - 1);
				if (i == length) {
					final Codec valueCodec = entry.getCodec();
					final var encoded = valueCodec.encodeStart(JanksonOps.INSTANCE, entry.getValue());
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
