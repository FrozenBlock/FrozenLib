/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.config.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import java.util.Collection;

public final class ConfigCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

		if (configs.size() == 1)
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.single", modId), true);
		else
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.multiple", configs.size(), modId), true);
		return 1;
	}
}
