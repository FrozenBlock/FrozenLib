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

package net.frozenblock.lib.config.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Collection;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public final class ConfigCommand {
	private ConfigCommand() {}

	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("frozenlib_config")
			.then(Commands.literal("reload")
				.then(Commands.argument("modId", StringArgumentType.string())
					.executes(context -> reloadConfigs(context.getSource(), StringArgumentType.getString(context, "modId")))
				)
			)
		);
	}

	private static int reloadConfigs(CommandSourceStack source, String modId) {
		Collection<Config<?>> configs = ConfigRegistry.getConfigsForMod(modId);
		for (Config<?> config : configs) {
			config.load();
		}
		for (ServerPlayer player : PlayerLookup.all(source.getServer())) {
			ConfigSyncPacket.sendS2C(player, configs);
		}

		if (configs.size() == 1)
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.single", modId), true);
		else
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.multiple", configs.size(), modId), true);
		return configs.size();
	}
}
