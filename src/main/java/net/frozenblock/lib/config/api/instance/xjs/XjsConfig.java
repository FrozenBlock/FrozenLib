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

package net.frozenblock.lib.config.api.instance.xjs;

import java.nio.file.Files;
import java.nio.file.Path;
import net.frozenblock.lib.config.api.instance.Config;
import xjs.data.JsonValue;
import xjs.data.serialization.writer.ValueWriter;

/**
 * Serializes and deserializes config data with GSON and Jankson.
 */
public class XjsConfig<T> extends Config<T> {

	private final XjsFormat format;

	public XjsConfig(String modId, Class<T> config) {
		this(modId, config, true);
	}

	public XjsConfig(String modId, Class<T> config, XjsFormat type) {
		this(modId, config, type, true);
	}

	public XjsConfig(String modId, Class<T> config, Path path, XjsFormat type) {
		this(modId, config, path, type, true);
	}

	public XjsConfig(String modId, Class<T> config, boolean supportsModification) {
		this(modId, config, XjsFormat.XJS_FORMATTED, supportsModification);
	}

	public XjsConfig(String modId, Class<T> config, XjsFormat type, boolean supportsModification) {
		this(modId, config, makePath(modId, type.getSerializedName()), type, supportsModification);
	}

	public XjsConfig(String modId, Class<T> config, Path path, XjsFormat type, boolean supportsModification) {
		super(modId, config, path, supportsModification, null, null);

		this.format = type;

		if (this.load()) {
			this.save();
		}
	}

	@Override
	public void onSave() throws Exception {
		Files.createDirectories(this.path().getParent());
		JsonValue value = XjsObjectMapper.toJsonObject(this.instance());
		try (
			ValueWriter writer = this.format.createWriter(this.path().toFile())
		) {
			writer.write(value);
		}
	}

	@Override
	public boolean onLoad() throws Exception {
		if (Files.exists(this.path())) {
			this.setConfig(XjsObjectMapper.deserializeObject(this.modId(), this.path(), this.configClass()));
		}
		return true;
	}
}
