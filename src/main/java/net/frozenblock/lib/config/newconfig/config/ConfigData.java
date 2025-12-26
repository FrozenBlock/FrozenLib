package net.frozenblock.lib.config.newconfig.config;

import java.util.Optional;
import net.frozenblock.lib.config.newconfig.ConfigSerializer;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ConfigData<T> {
	private final Identifier id;
	private final ConfigSettings<T> settings;
	private boolean loaded;

	public ConfigData(Identifier id, ConfigSettings<T> settings) {
		this.id = id;
		this.settings = settings;
	}

	public static <T> ConfigData<T> createAndRegister(Identifier id, ConfigSettings<T> settings) {
		return Registry.register(FrozenLibRegistries.CONFIG_DATA, id, new ConfigData<>(id, settings));
	}

	public Identifier id() {
		return this.id;
	}

	public ConfigSettings<T> settings() {
		return this.settings;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public void load(boolean checkIfCurrentlyLoaded) {
		if (checkIfCurrentlyLoaded && this.loaded) return;
		ConfigSerializer.loadConfig(this.id, Optional.empty());
		this.loaded = true;
	}

}
