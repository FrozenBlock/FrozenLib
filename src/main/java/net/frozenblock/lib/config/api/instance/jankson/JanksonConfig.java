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

package net.frozenblock.lib.config.api.instance.jankson;

import blue.endless.jankson.Jankson;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Serializes and deserializes config data with Jankson (Json5).
 * @deprecated Currently experimental and not finished.
 */
@Deprecated
class JanksonConfig<T> extends Config<T> {

	public static final String EXTENSION = "json5";

	private final Jankson jankson;

	public JanksonConfig(String modId, Class<T> config) {
		this(modId, config, Jankson.builder().build());
	}

	public JanksonConfig(String modId, Class<T> config, Jankson jankson) {
		this(modId, config, makePath(modId, EXTENSION), jankson);
	}

	public JanksonConfig(String modId, Class<T> config, Path path, Jankson jankson) {
		super(modId, config, path);
		this.jankson = jankson;

		if (this.load()) {
			this.save();
		}
	}

	@Override
	public void save() {
		FrozenMain.LOGGER.info("Saving config {}", this.configClass().getSimpleName());
		try {
			Files.createDirectories(this.path().getParent());
			BufferedWriter writer = Files.newBufferedWriter(this.path(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			writer.write(this.jankson.toJson(this.config()).toJson(true, true));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean load() {
		FrozenMain.LOGGER.info("Loading config {}", this.configClass().getSimpleName());
		if (Files.exists(this.path())) {
			try {
				this.setConfig(this.jankson.fromJson(this.jankson.load(this.path().toFile()), this.configClass()));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
