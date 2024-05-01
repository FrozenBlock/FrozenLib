/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.Jankson;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import org.jetbrains.annotations.Nullable;

/**
 * Serializes and deserializes config data with Jankson.
 */
public class JsonConfig<T> extends Config<T> {

	private final Jankson jankson;

	private final JsonType type;

	public JsonConfig(String modId, Class<T> config, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, true, dataFixer, version);
	}

	public JsonConfig(String modId, Class<T> config, JsonType type, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, type, true, dataFixer, version);
	}

	public JsonConfig(String modId, Class<T> config, Path path, JsonType type, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, path, type, true, dataFixer, version);
	}

	public JsonConfig(String modId, Class<T> config, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, JsonType.JSON, supportsModification, dataFixer, version);
	}

	public JsonConfig(String modId, Class<T> config, JsonType type, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, makePath(modId, type.getSerializedName()), type, supportsModification, dataFixer, version);
	}

	public JsonConfig(String modId, Class<T> config, Path path, JsonType type, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		super(modId, config, path, supportsModification, dataFixer, version);
		var janksonBuilder = Jankson.builder().withFixer(dataFixer).withVersion(version);

		this.jankson = ConfigSerialization.createJankson(janksonBuilder, modId);
		this.type = type;

		if (this.load()) {
			this.save();
		}
	}

	@Override
	public void onSave() throws Exception {
		Files.createDirectories(this.path().getParent());
		try (BufferedWriter writer = Files.newBufferedWriter(this.path(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write(this.jankson.toJson(this.instance()).toJson(this.type.getGrammar()));
		}
	}

	@Override
	public boolean onLoad() throws Exception {
		if (Files.exists(this.path())) {
			this.setConfig(this.jankson.fromJson(this.jankson.load(this.path().toFile()), this.configClass()));
		}
		return true;
	}
}
