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

import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import xjs.compat.serialization.util.UBTyping;
import xjs.compat.serialization.writer.HjsonWriter;
import xjs.compat.serialization.writer.TxtWriter;
import xjs.compat.serialization.writer.UbjsonWriter;
import xjs.data.serialization.writer.DjsWriter;
import xjs.data.serialization.writer.JsonWriter;
import xjs.data.serialization.writer.ValueWriter;

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
	 * Prints unformatted DJS with no whitespace.
	 */
	DJS("djs", writer -> {
		try {
			return new DjsWriter(writer, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}),

	/**
	 * Pretty prints DJS with whitespace.
	 */
	DJS_FORMATTED(DJS.getSerializedName(), writer -> {
		try {
			return new DjsWriter(writer, true);
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
