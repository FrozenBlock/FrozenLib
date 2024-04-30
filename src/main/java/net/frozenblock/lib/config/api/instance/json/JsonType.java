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

package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonGrammar;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum JsonType implements StringRepresentable {
	JSON("json", JsonGrammar.STRICT),
	JSON5("json5", JsonGrammar.JSON5),
	/**
	 * Like JSON5 but it supports not having quotes on keys
	 */
	JSON5_UNQUOTED_KEYS("json5", ConfigSerialization.JSON5_UNQUOTED_KEYS);

	@NotNull
	private final String name;

	@NotNull
	private final JsonGrammar grammar;

	JsonType(@NotNull String name, @NotNull JsonGrammar grammar) {
		this.name = name;
		this.grammar = grammar;
	}

	@Override
	@NotNull
	public String getSerializedName() {
		return this.name;
	}

	@NotNull
	public JsonGrammar getGrammar() {
		return this.grammar;
	}
}
