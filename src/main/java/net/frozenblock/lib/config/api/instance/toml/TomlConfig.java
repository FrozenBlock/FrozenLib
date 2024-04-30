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

package net.frozenblock.lib.config.api.instance.toml;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.frozenblock.lib.config.api.instance.Config;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes and deserializes config data with TOML4J.
 * @since 1.4
 */
@ApiStatus.Experimental
public class TomlConfig<T> extends Config<T> {

	public static final String EXTENSION = "toml";

	private final TomlWriter tomlWriter;


	public TomlConfig(String modId, Class<T> config) {
		this(modId, config, new TomlWriter.Builder());
	}

	public TomlConfig(String modId, Class<T> config, TomlWriter.Builder builder) {
		this(modId, config, makePath(modId, EXTENSION), builder);
	}

	public TomlConfig(String modId, Class<T> config, Path path, TomlWriter.@NotNull Builder builder) {
		super(modId, config, path, true, null, null);
		this.tomlWriter = builder.build();

		if (this.load()) {
			this.save();
		}
	}

	@Override
	public void onSave() throws Exception {
		Files.createDirectories(this.path().getParent());
		BufferedWriter writer = Files.newBufferedWriter(this.path(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		this.tomlWriter.write(this.instance(), writer);
		writer.close();
	}

	@Override
	public boolean onLoad() throws Exception {
		if (Files.exists(this.path())) {
			var tomlReader = getDefaultToml();
			try (var reader = Files.newBufferedReader(this.path())) {
				this.setConfig(tomlReader.read(reader).to(this.configClass()));
			}
		}
		return true;
	}

	@NotNull
	private Toml getDefaultToml() {
		Toml toml = new Toml();
		return new Toml(toml.read(tomlWriter.write(defaultInstance())));
	}
}
