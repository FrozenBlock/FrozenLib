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

package net.frozenblock.lib.wind.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.frozenblock.lib.wind.api.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class OverrideWindCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("wind").requires(source -> source.hasPermission(2))
				.then(Commands.argument("x", DoubleArgumentType.doubleArg()).then(Commands.argument("y", DoubleArgumentType.doubleArg()).then(Commands.argument("z", DoubleArgumentType.doubleArg()).executes(context -> setWind(context.getSource(), DoubleArgumentType.getDouble(context, "x"), DoubleArgumentType.getDouble(context, "y"), DoubleArgumentType.getDouble(context, "z"))))))
				.then(Commands.argument("overrideEnabled", BoolArgumentType.bool()).executes(context -> setWind(context.getSource(), BoolArgumentType.getBool(context, "overrideEnabled"))))
		);
	}

	private static int setWind(CommandSourceStack source, boolean bl) {
		ServerLevel level = source.getLevel();
		WindManager windManager = WindManager.getWindManager(level);
		windManager.overrideWind = bl;
		windManager.sendSync(level);
		source.sendSuccess(Component.translatable("commands.wind.toggle.success", bl), true);
		return 1;
	}

	private static int setWind(CommandSourceStack source, double x, double y, double z) {
		ServerLevel level = source.getLevel();
		WindManager windManager = WindManager.getWindManager(level);
		windManager.overrideWind = true;
		windManager.windX = x;
		windManager.windY = y;
		windManager.windZ = z;
		windManager.commandWind = new Vec3(windManager.windX, windManager.windY, windManager.windZ);
		windManager.sendSync(level);
		source.sendSuccess(Component.translatable("commands.wind.success", x, y, z), true);
		return 1;
	}

}
