package net.frozenblock.lib.config.api.instance.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.ConfigExclusionStrategy;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Serializes and deserializes config data with GSON.
 */
public class GsonConfig<T> extends Config<T> {
	private final Gson gson;
	private final Path path;

	public GsonConfig(String modId, Class<T> config) {
		this(modId, config, new GsonBuilder());
	}

	public GsonConfig(String modId, Class<T> config, GsonBuilder builder) {
		this(modId, config, Path.of("./config/" + modId + ".json"), builder);
	}

	public GsonConfig(String modId, Class<T> config, Path path, GsonBuilder builder) {
		super(modId, config);
		this.gson = builder.setExclusionStrategies(new ConfigExclusionStrategy())
				.registerTypeHierarchyAdapter(TypedEntry.class, new TypedEntry.Serializer())
				.registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeHierarchyAdapter(Color.class, new ColorSerializer())
				.serializeNulls()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.setPrettyPrinting()
				.create();
		this.path = path;
	}

	@Override
	public void save() {
		FrozenMain.LOGGER.info("Saving config {}", this.configClass().getSimpleName());
		try {
			Files.writeString(this.path, this.gson.toJson(config()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		FrozenMain.LOGGER.info("Loading config {}", this.configClass().getSimpleName());
		try {
			if (Files.notExists(this.path)) {
				this.save();
				return;
			}

			this.setConfig(this.gson.fromJson(Files.readString(this.path), configClass()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Path path() {
		return this.path;
	}
}
