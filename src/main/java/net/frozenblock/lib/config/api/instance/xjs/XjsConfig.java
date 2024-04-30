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
