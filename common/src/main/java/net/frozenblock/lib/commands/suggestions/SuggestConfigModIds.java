/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SerializableCustomSuggestion;
import net.minecraft.commands.suggestions.SuggestionContext;

public class SuggestConfigModIds implements SerializableCustomSuggestion {
	private static final SuggestConfigModIds INSTANCE = new SuggestConfigModIds();
	public static final SerializableCustomSuggestion.Type<SuggestConfigModIds> TYPE = SerializableCustomSuggestion.singletonType(INSTANCE);

	@Override
	public Type<? extends SerializableCustomSuggestion> type() {
		return TYPE;
	}

	public static SuggestionProvider<CommandSourceStack> suggestConfigModIds() {
		return INSTANCE.toBrigadier();
	}

	@Override
	public CompletableFuture<Suggestions> generate(CommandContext<? extends SuggestionContext> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		return SuggestionContext.suggest(
			ConfigV2Registry.allConfigData().stream()
				.map(configData -> configData.id().namespace())
				.toList(),
			builder
		);
	}
}
