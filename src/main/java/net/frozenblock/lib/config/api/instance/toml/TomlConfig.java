package net.frozenblock.lib.config.api.instance.toml;

import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.Toml;
import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.TomlWriter;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Serializes and deserializes config data with TOML4J.
 */
public class TomlConfig<T> extends Config<T> {

	public static final String EXTENSION = "toml";

	private final TomlWriter tomlWriter;


	public TomlConfig(String modId, Class<T> config) {
		this(modId, config, new TomlWriter());
	}

	public TomlConfig(String modId, Class<T> config, TomlWriter writer) {
		this(modId, config, makePath(modId, EXTENSION), writer);
	}

	public TomlConfig(String modId, Class<T> config, Path path, TomlWriter writer) {
		super(modId, config, path);
		this.tomlWriter = writer;

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
			this.tomlWriter.write(this.config(), writer);
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
