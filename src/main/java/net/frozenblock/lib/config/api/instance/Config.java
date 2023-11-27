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

import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.network.ConfigSyncPacket;
import org.jetbrains.annotations.Nullable;

public abstract class Config<T> {

	private final String modId;
	private final Path path;
	private final boolean supportsModification;
	@Nullable
	private final DataFixer dataFixer;
	@Nullable
	private final Integer version;
	private final Class<T> config;
	private T configInstance;
	private final T defaultInstance;
	private ConfigModification.ModificationType modificationType = ConfigModification.ModificationType.NONE;

	protected Config(String modId, Class<T> config, Path path, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this.modId = modId;
		this.path = path;
		this.supportsModification = supportsModification;
		this.config = config;
		try {
			this.defaultInstance = this.configInstance = config.getConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("No default constructor for default config instance.");
		}
		this.dataFixer = dataFixer;
		this.version = version;
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

	public boolean supportsModification() {
		return this.supportsModification;
	}

	@Nullable
	public DataFixer dataFixer() {
		return this.dataFixer;
	}

	@Nullable
	public Integer version() {
		return this.version;
	}

	/**
	 * @return The current config instance with modifications if applicable
	 */
	public T config() {
		if (this.supportsModification()) return ConfigModification.modifyConfig(this, this.instance(), false);
		return this.instance();
	}

	/**
	 * @return The current config instance with config sync modifications
	 * @throws IllegalStateException If the config does not support modification
	 * @since 1.4.5
	 */
	public T configWithSync() throws IllegalStateException {
		if (!this.supportsModification()) {
			String formatted = String.format("Config %s from %s", this.configClass().getSimpleName(), this.modId());
			FrozenLogUtils.logWarning(formatted + " does not support modification, returning unmodified instance.");
			return this.instance();
		}
		return ConfigModification.modifyConfig(this, this.instance(), true);
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

	public void setModificationType(ConfigModification.ModificationType modificationType) {
		this.modificationType = modificationType;
	}

	public ConfigModification.ModificationType getModificationType() {
		return this.modificationType;
	}

	protected abstract void onSave() throws Exception;
	public abstract boolean onLoad() throws Exception;

	public final void save() {
		String formatted = String.format("config %s from %s", this.configClass().getSimpleName(), this.modId());
		FrozenSharedConstants.LOGGER.info("Saving " + formatted);
		try {
			this.onSave();
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
				ConfigSyncPacket.trySendC2S(this);
		} catch (Exception e) {
			FrozenLogUtils.logError("Error while saving " + formatted, true, e);
		}
	}

	public final boolean load() {
		String formatted = String.format("config %s from %s", this.configClass().getSimpleName(), this.modId());
		FrozenSharedConstants.LOGGER.info("Loading " + formatted);
		try {
			return this.onLoad();
		} catch (Exception e) {
			FrozenLogUtils.logError("Error while loading " + formatted, true, e);
			return false;
		}
	}

}
