/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
 * Serializes and deserializes config data with GSON and Jankson.
 */
public class JsonConfig<T> extends Config<T> {

	private final Jankson jankson;

	private final JsonType type;

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config) {
		this(modId, config, (@Nullable DataFixer) null, null);
	}

	public JsonConfig(String modId, Class<T> config, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, true, dataFixer, version);
	}

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config, JsonType type) {
		this(modId, config, type, null, null);
	}

	public JsonConfig(String modId, Class<T> config, JsonType type, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, type, true, dataFixer, version);
	}

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config, Path path, JsonType type) {
		this(modId, config, path, type, null, null);
	}

	public JsonConfig(String modId, Class<T> config, Path path, JsonType type, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, path, type, true, dataFixer, version);
	}

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config, boolean supportsModification) {
		this(modId, config, supportsModification, null, null);
	}

	public JsonConfig(String modId, Class<T> config, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, JsonType.JSON, supportsModification, dataFixer, version);
	}

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config, JsonType type, boolean supportsModification) {
		this(modId, config, type, supportsModification, null, null);
	}

	public JsonConfig(String modId, Class<T> config, JsonType type, boolean supportsModification, @Nullable DataFixer dataFixer, @Nullable Integer version) {
		this(modId, config, makePath(modId, type.getSerializedName()), type, supportsModification, dataFixer, version);
	}

	@Deprecated(forRemoval = true)
	public JsonConfig(String modId, Class<T> config, Path path, JsonType type, boolean supportsModification) {
		this(modId, config, path, type, supportsModification, null, null);
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
