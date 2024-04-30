/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.instance;

import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.registry.ConfigLoadEvent;
import net.frozenblock.lib.config.api.registry.ConfigSaveEvent;
import net.frozenblock.lib.config.api.sync.annotation.UnsyncableConfig;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Config<T> {

	private final String modId;
	private final Path path;
	private final boolean supportsModification;
	@Nullable
	private final DataFixer dataFixer;
	@Nullable
	private final Integer version;
	private final Class<T> configClass;
	private T configInstance;
	private final T defaultInstance;
	private boolean synced = false;

	protected Config(String modId, Class<T> configClass, Path path, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this.modId = modId;
		this.path = path;
		this.supportsModification = supportsModification;
		this.configClass = configClass;
		try {
			this.defaultInstance = this.configInstance = configClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("No default constructor for default config instance.", e);
		}
		this.dataFixer = dataFixer;
		this.version = version;
	}

	@NotNull
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
	 * @since 1.5
	 */
	public T configWithSync() {
		if (!this.supportsSync()) {
			//TODO: Possibly remove before release? This causes log spam. Up to you, Tree. Might be best with JavaDoc instead.
			String formatted = String.format("Config %s from %s", this.configClass().getSimpleName(), this.modId());
			FrozenLogUtils.logWarning(formatted + " does not support modification, returning unmodified instance.");
			return this.instance();
		}
		return ConfigModification.modifyConfig(this, this.instance(), true);
	}

	/**
	 * @return If the current config supports modification and does not have the {@link UnsyncableConfig} annotation.
	 * @since 1.5
	 */
	public boolean supportsSync() {
		return this.supportsModification() && !this.configClass().isAnnotationPresent(UnsyncableConfig.class);
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
		return this.configClass;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	public boolean isSynced() {
		return this.synced;
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
        FrozenSharedConstants.LOGGER.info("Saving {}", formatted);
		try {
			this.onSave();

			if (FrozenBools.isInitialized) {
				if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
					ConfigSyncPacket.trySendC2S(this);

				invokeSaveEvents();
			}
		} catch (Exception e) {
			FrozenLogUtils.logError("Error while saving " + formatted, e);
		}
	}

	public final boolean load() {
		String formatted = this.formattedName();
		FrozenSharedConstants.LOGGER.info("Loading " + formatted);
		try {
			boolean loadVal = this.onLoad();

			if (FrozenBools.isInitialized) {
				invokeLoadEvents();
			}
			return loadVal;
		} catch (Exception e) {
			FrozenLogUtils.logError("Error while loading " + formatted, e);
			return false;
		}
	}

	private void invokeSaveEvents() {
		String formatted = this.formattedName();
		try {
			ConfigSaveEvent.EVENT.invoker().onSave(this);

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				ConfigSaveEvent.Client.EVENT.invoker().onSave(this);
			}
		} catch (Exception e) {
			FrozenLogUtils.logError("Error in config save events for " + formatted, e);
		}
	}

	private void invokeLoadEvents() {
		String formatted = this.formattedName();
		try {
			ConfigLoadEvent.EVENT.invoker().onLoad(this);

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				ConfigLoadEvent.Client.EVENT.invoker().onLoad(this);
			}
		} catch (Exception e) {
			FrozenLogUtils.logError("Error in config load events for " + formatted, e);
		}
	}
}
