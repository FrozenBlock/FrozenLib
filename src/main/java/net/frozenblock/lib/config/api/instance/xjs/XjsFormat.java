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

package net.frozenblock.lib.config.api.instance.xjs;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import xjs.compat.serialization.util.UBTyping;
import xjs.compat.serialization.writer.HjsonWriter;
import xjs.compat.serialization.writer.JsonWriter;
import xjs.compat.serialization.writer.TxtWriter;
import xjs.compat.serialization.writer.UbjsonWriter;
import xjs.data.serialization.writer.ValueWriter;
import xjs.data.serialization.writer.DjsWriter;

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
