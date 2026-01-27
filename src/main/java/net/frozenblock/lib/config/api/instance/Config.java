/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.config.api.instance;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.registry.ConfigLoadEvent;
import net.frozenblock.lib.config.api.registry.ConfigSaveEvent;
import org.jetbrains.annotations.Contract;

public abstract class Config<T> {
	private final String modId;
	private final Path path;
	private final boolean supportsModification;
	private final Class<T> configClass;
	private T configInstance;
	private final T defaultInstance;

	protected Config(String modId, Class<T> configClass, Path path, boolean supportsModification) {
		this.modId = modId;
		this.path = path;
		this.supportsModification = supportsModification;
		this.configClass = configClass;
		try {
			this.defaultInstance = this.configInstance = configClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("No default constructor for default config instance.", e);
		}
	}

	@Contract(pure = true)
	public static Path makePath(String modId, String extension) {
		return Path.of("./config/" + modId + "." + extension);
	}

	public String modId() {
		return this.modId;
	}

	public Path path() {
		return this.path;
	}

	public boolean supportsModification() {
		return this.supportsModification;
	}

	/**
	 * @return The current config instance with modifications if applicable.
	 */
	public T config() {
		if (this.supportsModification()) return ConfigModification.modifyConfig(this, this.instance(), false);
		return this.instance();
	}

	/**
	 * @return The unmodified current config instance.
	 */
	public T instance() {
		return this.configInstance;
	}

	public void setConfig(T configInstance) {
		this.configInstance = configInstance;
	}

	public T defaultInstance() {
		return this.defaultInstance;
	}

	public Class<T> configClass() {
		return this.configClass;
	}

	/**
	 * @since 1.5
	 */
	protected String formattedName() {
		return String.format("config %s from %s", this.configClass().getSimpleName(), this.modId());
	}

	protected abstract void onSave() throws Exception;

	protected abstract boolean onLoad() throws Exception;

	public final void save() {
		String formatted = this.formattedName();
		FrozenLibLogUtils.LOGGER.info("Saving {}", formatted);
		try {
			this.onSave();

			if (FrozenBools.isInitialized) {
				invokeSaveEvents();
			}
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error while saving " + formatted, e);
		}
	}

	public final boolean load() {
		String formatted = this.formattedName();
		FrozenLibLogUtils.LOGGER.info("Loading {}", formatted);
		try {
			final boolean loadVal = this.onLoad();
			if (FrozenBools.isInitialized) invokeLoadEvents();
			return loadVal;
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error while loading " + formatted, e);
			return false;
		}
	}

	private void invokeSaveEvents() {
		String formatted = this.formattedName();
		try {
			ConfigSaveEvent.EVENT.invoker().onSave(this);
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ConfigSaveEvent.Client.EVENT.invoker().onSave(this);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error in config save events for " + formatted, e);
		}
	}

	private void invokeLoadEvents() {
		String formatted = this.formattedName();
		try {
			ConfigLoadEvent.EVENT.invoker().onLoad(this);
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ConfigLoadEvent.Client.EVENT.invoker().onLoad(this);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error in config load events for " + formatted, e);
		}
	}
}
