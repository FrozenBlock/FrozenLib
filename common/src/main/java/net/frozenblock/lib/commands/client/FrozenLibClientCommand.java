/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.v2.impl.client.ClientConfigCommand;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;

@ClientOnly
@UtilityClass
public final class FrozenLibClientCommand {

	public static void register(
		CommandDispatcher<SuggestionContext> dispatcher,
		Function<String, LiteralArgumentBuilder<SuggestionContext>> literal,
		BiFunction<String, StringArgumentType, RequiredArgumentBuilder<SuggestionContext, ?>> argument,
		Consumer<Component> feedbackCallback
	) {
		dispatcher.register(
			literal.apply("frozenlib_client")
				.then(ClientConfigCommand.buildSubCommand(literal, argument, feedbackCallback))
				.then(PanoramaCommand.buildSubCommand(literal))
		);
	}
}
