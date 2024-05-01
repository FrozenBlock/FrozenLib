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
