/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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

public class WindOverrideCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("windoverride").requires(source -> source.hasPermission(2))
				.then(Commands.argument("x", DoubleArgumentType.doubleArg()).then(Commands.argument("y", DoubleArgumentType.doubleArg()).then(Commands.argument("z", DoubleArgumentType.doubleArg()).executes(context -> setWind(context.getSource(), DoubleArgumentType.getDouble(context, "x"), DoubleArgumentType.getDouble(context, "y"), DoubleArgumentType.getDouble(context, "z"))))))
				.then(Commands.argument("overrideEnabled", BoolArgumentType.bool()).executes(context -> setWind(context.getSource(), BoolArgumentType.getBool(context, "overrideEnabled"))))
		);
	}

	private static int setWind(CommandSourceStack source, boolean bl) {
		ServerLevel level = source.getLevel();
		WindManager windManager = WindManager.getWindManager(level);
		windManager.overrideWind = bl;
		windManager.sendSync(level);
		source.sendSuccess(() -> Component.translatable("commands.wind.toggle.success", bl), true);
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
		source.sendSuccess(() -> Component.translatable("commands.wind.success", x, y, z), true);
		return 1;
	}

}
