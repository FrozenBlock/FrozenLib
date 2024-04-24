/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.config.api.instance.xjs;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import blue.endless.jankson.JsonElement;
import com.mojang.serialization.Codec;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JanksonOps;
import xjs.core.JsonFormat;
import xjs.core.JsonValue;
import xjs.serialization.writer.ElementWriter;

/**
 * Serializes and deserializes config data with GSON and Jankson.
 */
public class XjsConfig<T> extends Config<T> {

	private final Codec<T> codec;
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
			ElementWriter elementWriter = this.format.createWriter(this.path().toFile())
		) {
			elementWriter.write(value);
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
