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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class ClientConfigCommand {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(
			ClientCommands.literal("frozenlib_config_client")
				.then(ClientCommands.literal("reload")
					.then(ClientCommands.argument("modId", StringArgumentType.string())
						.suggests((context, builder) ->
								SharedSuggestionProvider.suggest(
									ConfigV2Registry.allConfigData().stream()
										.map(configData -> configData.id().namespace())
										.toList(),
									builder
								)
						)
						.executes(context -> reloadConfigs(context.getSource(), StringArgumentType.getString(context, "modId")))
					)
				)
		);
	}

	private static int reloadConfigs(FabricClientCommandSource source, String modId) {
		final int configCount = ConfigCommand.reloadConfigsAndCount(modId);
		if (configCount == 1) {
			source.sendFeedback(Component.translatable("commands.frozenlib_config.reload.single", modId));
		} else {
			source.sendFeedback(Component.translatable("commands.frozenlib_config.reload.multiple", configCount, modId));
		}
		return configCount;
	}
}
