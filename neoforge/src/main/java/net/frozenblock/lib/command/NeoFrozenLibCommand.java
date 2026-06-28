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

package net.frozenblock.lib.command;

import com.mojang.brigadier.CommandDispatcher;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.entity.api.command.ScaleEntityCommand;
import net.frozenblock.lib.screenshake.api.command.ScreenShakeCommand;
import net.frozenblock.lib.tag.api.TagListCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class NeoFrozenLibCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("frozenlib")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(ConfigCommand.buildSubCommand())
			.then(ScaleEntityCommand.buildSubCommand())
			.then(ScreenShakeCommand.buildSubCommand())
			.then(TagListCommand.buildSubCommand())
		);
	}
}

