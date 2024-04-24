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

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import xjs.serialization.util.UBTyping;
import xjs.serialization.writer.HjsonWriter;
import xjs.serialization.writer.JsonWriter;
import xjs.serialization.writer.TxtWriter;
import xjs.serialization.writer.UbjsonWriter;
import xjs.serialization.writer.ValueWriter;
import xjs.serialization.writer.XjsWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.function.Function;

public enum XjsFormat implements StringRepresentable {
	/**
	 * Prints unformatted, regular JSON with no whitespace.
	 */
	JSON("json", writer -> {
		try {
			return new JsonWriter(writer, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Pretty prints regular JSON with whitespace.
	 */
	JSON_FORMATTED(JSON.getSerializedName(), writer -> {
		try {
			return new JsonWriter(writer, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Prints unformatted HJSON with no whitespace.
	 */
	HJSON("hjson", writer -> {
		try {
			return new HjsonWriter(writer, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Pretty prints HJSON with whitespace.
	 */
	HJSON_FORMATTED(HJSON.getSerializedName(), writer -> {
		try {
			return new HjsonWriter(writer, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Prints unformatted XJS with no whitespace.
	 */
	XJS("xjs", writer -> {
		try {
			return new XjsWriter(writer, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Pretty prints XJS with whitespace.
	 */
	XJS_FORMATTED(XJS.getSerializedName(), writer -> {
		try {
			return new XjsWriter(writer, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Prints into a txt file.
	 */
	TXT("txt", writer -> {
		try {
			return new TxtWriter(writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Prints Universal Binary JSON.
	 */
	UBJSON("ubjson", writer -> {
		try {
			return new UbjsonWriter(writer, UBTyping.BALANCED);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	});

	@NotNull
	private final String name;

	@NotNull
	private final Function<File, ValueWriter> writer;

	XjsFormat(@NotNull String name, @NotNull Function<File, ValueWriter> writer) {
		this.name = name;
		this.writer = writer;
	}

	@Override
	@NotNull
	public String getSerializedName() {
		return this.name;
	}

	@NotNull
	public ValueWriter createWriter(File writer) {
		return this.writer.apply(writer);
	}
}
