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

package net.frozenblock.lib.config.api.instance.toml;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.instance.Config;

/**
 * Serializes and deserializes config data with TOML4J.
 * @deprecated Currently experimental and not finished.
 */
@Deprecated
class TomlConfig<T> extends Config<T> {

	public static final String EXTENSION = "toml";

	private final TomlWriter tomlWriter;


	public TomlConfig(String modId, Class<T> config) {
		this(modId, config, new TomlWriter.Builder());
	}

	public TomlConfig(String modId, Class<T> config, TomlWriter.Builder builder) {
		this(modId, config, makePath(modId, EXTENSION), builder);
	}

	public TomlConfig(String modId, Class<T> config, Path path, TomlWriter.Builder builder) {
		super(modId, config, path, true);
		this.tomlWriter = builder.build();

		if (this.load()) {
			this.save();
		}
	}

	@Override
	public void save() {
		FrozenSharedConstants.LOGGER.info("Saving config {}", this.configClass().getSimpleName());
		try {
			Files.createDirectories(this.path().getParent());
			BufferedWriter writer = Files.newBufferedWriter(this.path(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			this.tomlWriter.write(this.config(), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean load() {
		FrozenSharedConstants.LOGGER.info("Loading config {}", this.configClass().getSimpleName());
		if (Files.exists(this.path())) {
			try {
				var tomlReader = new Toml();
				var reader = Files.newBufferedReader(this.path());
				this.setConfig(tomlReader.read(reader).to(this.configClass()));
				reader.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
