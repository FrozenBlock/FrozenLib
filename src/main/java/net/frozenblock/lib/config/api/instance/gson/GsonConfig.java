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

package net.frozenblock.lib.config.api.instance.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.ConfigExclusionStrategy;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Serializes and deserializes config data with GSON.
 */
public class GsonConfig<T> extends Config<T> {
	private final Gson gson;
	private final Path path;

	public GsonConfig(String modId, Class<T> config) {
		this(modId, config, new GsonBuilder());
	}

	public GsonConfig(String modId, Class<T> config, GsonBuilder builder) {
		this(modId, config, Path.of("./config/" + modId + ".json"), builder);
	}

	public GsonConfig(String modId, Class<T> config, Path path, GsonBuilder builder) {
		super(modId, config);
		this.gson = builder.setExclusionStrategies(new ConfigExclusionStrategy())
				.registerTypeHierarchyAdapter(TypedEntry.class, new TypedEntrySerializer<>(modId))
				.registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeHierarchyAdapter(Color.class, new ColorSerializer())
				.serializeNulls()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.setPrettyPrinting()
				.create();
		this.path = path;

		if (this.load()) {
			this.save();
		};
	}

	@Override
	public void save() {
		FrozenMain.LOGGER.info("Saving config {}", this.configClass().getSimpleName());
		try {
			Files.createDirectories(this.path.getParent());
			BufferedWriter writer = Files.newBufferedWriter(this.path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			this.gson.toJson(this.config(), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean load() {
		FrozenMain.LOGGER.info("Loading config {}", this.configClass().getSimpleName());
		if (Files.exists(this.path)) {
			try {
				var reader = Files.newBufferedReader(this.path);
				this.setConfig(this.gson.fromJson(reader, this.configClass()));
				reader.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

	public Path path() {
		return this.path;
	}
}
