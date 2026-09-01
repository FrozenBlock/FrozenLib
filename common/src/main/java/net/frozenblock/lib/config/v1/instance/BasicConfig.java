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

package net.frozenblock.lib.config.v1.instance;

import java.nio.file.Path;
import net.frozenblock.lib.FrozenLibLogUtils;

public abstract class BasicConfig<T> {
	private final String modId;
	private final Path path;
	private final Class<T> configClass;
	private T configInstance;
	private final T defaultInstance;

	protected BasicConfig(String modId, Class<T> configClass, Path path) {
		this.modId = modId;
		this.path = path;
		this.configClass = configClass;
		try {
			this.defaultInstance = this.configInstance = configClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("No default constructor for default config instance.", e);
		}
	}

	public static Path makePath(String modId, String extension) {
		return Path.of("./config/" + modId + "." + extension);
	}

	public String modId() {
		return this.modId;
	}

	public Path path() {
		return this.path;
	}

	/**
	 * @return The current config instance.
	 */
	public T config() {
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
		final String formatted = this.formattedName();
		FrozenLibLogUtils.LOGGER.info("Saving {}", formatted);
		try {
			this.onSave();
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error while saving " + formatted, e);
		}
	}

	public final boolean load() {
		final String formatted = this.formattedName();
		FrozenLibLogUtils.LOGGER.info("Loading {}", formatted);
		try {
			return this.onLoad();
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Error while loading " + formatted, e);
			return false;
		}
	}
}
