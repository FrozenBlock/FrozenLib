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
