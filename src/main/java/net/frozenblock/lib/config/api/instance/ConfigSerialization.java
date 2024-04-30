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

package net.frozenblock.lib.config.api.instance;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import com.mojang.datafixers.DataFixer;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.json.JanksonTypedEntrySerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigSerialization {
	private ConfigSerialization() {}

	// JANKSON

	public static final JsonGrammar JSON5_UNQUOTED_KEYS = JsonGrammar.builder()
		.withComments(true)
		.printTrailingCommas(true)
		.bareSpecialNumerics(true)
		.printUnquotedKeys(true)
		.build();

	public static Jankson createJankson(@NotNull Jankson.Builder builder, String modId) {
		JanksonTypedEntrySerializer typedEntrySerializer = new JanksonTypedEntrySerializer(modId);
		return builder
			.registerSerializer(TypedEntry.class, typedEntrySerializer)
			.registerDeserializer(JsonElement.class, TypedEntry.class, typedEntrySerializer)
			.build();
	}

	public static Jankson createJankson(String modId, @Nullable DataFixer dataFixer) {
		return createJankson(Jankson.builder().withFixer(dataFixer), modId);
	}

	public static Jankson createJankson(String modId) {
		return createJankson(modId, null);
	}
}
