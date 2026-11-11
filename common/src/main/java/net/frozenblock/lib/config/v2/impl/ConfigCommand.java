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

package net.frozenblock.lib.config.v2.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.commands.suggestions.SuggestConfigModIds;
import net.frozenblock.lib.config.v2.config.ConfigData;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.network.chat.Component;

@UtilityClass
public final class ConfigCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		return Commands.literal("config").then(
			Commands.literal("reload").then(
				Commands.argument("modId", StringArgument.word())
					.suggests(SuggestConfigModIds.suggestConfigModIds())
					.executes(context -> reloadConfigs(context.getSource(), StringArgument.getString(context, "modId")))
					.requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS))
			)
		);
	}

	public static int reloadConfigsAndCount(String modId) {
		final Collection<ConfigData<?>> configs = ConfigV2Registry.allConfigData().stream()
			.filter(data -> data.id().namespace().equals(modId))
			.toList();
		for (ConfigData<?> config : configs) config.reload();
		return configs.size();
	}

	private static int reloadConfigs(CommandSourceStack source, String modId) {
		final int configCount = reloadConfigsAndCount(modId);
		if (configCount == 1) {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.single", modId), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib_config.reload.multiple", configCount, modId), true);
		}
		return configCount;
	}
}
