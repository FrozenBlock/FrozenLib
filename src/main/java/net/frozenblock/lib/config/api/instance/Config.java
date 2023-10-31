/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.api.instance;

import net.frozenblock.lib.FrozenSharedConstants;
import java.nio.file.Path;

public abstract class Config<T> {

	private final String modId;
	private final Path path;
	private final boolean supportsModification;
	private final Class<T> config;
	private T configInstance;
	private final T defaultInstance;

	public Config(String modId, Class<T> config, Path path, boolean supportsModification) {
		this.modId = modId;
		this.path = path;
		this.supportsModification = supportsModification;
		this.config = config;
		try {
			this.defaultInstance = this.configInstance = config.getConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("No default constructor for default config instance.");
		}
	}

	protected static Path makePath(String modId, String extension) {
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
	 * @return The current config instance with modifications if applicable
	 */
	public T config() {
		if (this.supportsModification()) return ConfigModification.modifyConfig(this, this.instance());
		return this.instance();
	}

	/**
	 * @return The unmodified current config instance
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
		return this.config;
	}

	protected abstract void onSave();
	public abstract boolean onLoad();

	public final void save() {
		FrozenSharedConstants.LOGGER.info("Saving config {} from {}", this.configClass().getSimpleName(), this.modId());
		this.onSave();
	}

	public final boolean load() {
		FrozenSharedConstants.LOGGER.info("Loading config {} from {}", this.configClass().getSimpleName(), this.modId());
		return this.onLoad();
	}

}
