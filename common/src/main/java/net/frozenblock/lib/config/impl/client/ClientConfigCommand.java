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

package net.frozenblock.lib.config.impl.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

@ClientOnly
public final class ClientConfigCommand {

	public static LiteralArgumentBuilder<SharedSuggestionProvider> buildSubCommand(
		Function<String, LiteralArgumentBuilder<SharedSuggestionProvider>> literal,
		BiFunction<String, StringArgumentType, RequiredArgumentBuilder<SharedSuggestionProvider, ?>> argument,
		Consumer<Component> feedbackCallback
	) {
		return literal.apply("config").then(
			literal.apply("reload").then(
				argument.apply("modId", StringArgumentType.string()).suggests((context, builder) ->
						SharedSuggestionProvider.suggest(
							ConfigV2Registry.allConfigData().stream()
								.map(configData -> configData.id().namespace())
								.toList(),
							builder
						)
				).executes(context -> reloadConfigs(StringArgumentType.getString(context, "modId"), feedbackCallback))
			)
		);
	}

	private static int reloadConfigs(String modId, Consumer<Component> feedbackCallback) {
		final int configCount = ConfigCommand.reloadConfigsAndCount(modId);
		if (configCount == 1) {
			feedbackCallback.accept(Component.translatable("commands.frozenlib_config.reload.single", modId));
		} else {
			feedbackCallback.accept(Component.translatable("commands.frozenlib_config.reload.multiple", configCount, modId));
		}
		return configCount;
	}
}
