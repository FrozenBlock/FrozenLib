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
