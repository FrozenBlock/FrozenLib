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

package net.frozenblock.lib.wind.api.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Optional;
import net.frozenblock.lib.wind.v2.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class WindCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("wind")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

		final ArgumentBuilder<CommandSourceStack, ?> overrideBuilder = Commands.literal("override");
		overrideBuilder
			.then(Commands.argument("x", DoubleArgumentType.doubleArg())
				.then(Commands.argument("y", DoubleArgumentType.doubleArg())
					.then(Commands.argument("z", DoubleArgumentType.doubleArg())
						.executes(context -> setAndEnableWindOverride(
							context.getSource(),
							DoubleArgumentType.getDouble(context, "x"),
							DoubleArgumentType.getDouble(context, "y"),
							DoubleArgumentType.getDouble(context, "z")
						))
					)
				)
			)
			.then(Commands.argument("enabled", BoolArgumentType.bool())
				.executes(context -> toggleWindOverride(context.getSource(), BoolArgumentType.getBool(context, "enabled")))
			);

		final ArgumentBuilder<CommandSourceStack, ?> displayBuilder = Commands.literal("display");
		displayBuilder
			.then(Commands.literal("global").executes(context -> displayWindValue(context.getSource(), false)))
			.then(Commands.literal("pos").executes(context -> displayWindValue(context.getSource(), true)));

		return builder.then(overrideBuilder).then(displayBuilder);
	}

	private static int toggleWindOverride(CommandSourceStack source, boolean bl) {
		final ServerLevel level = source.getLevel();
		final WindManager windManager = WindManager.getOrCreateWindManager(level);
		if (windManager.overrideWind == bl) {
			source.sendSuccess(() -> Component.translatable("commands.wind.toggle.failure", bl ? "enabled" : "disabled"), true);
			return 0;
		}

		windManager.overrideWind = bl;
		windManager.sendSync(level);
		source.sendSuccess(() -> Component.translatable("commands.wind.toggle.success", bl ? "Enabled" : "Disabled"), true);
		return 1;
	}

	private static int setAndEnableWindOverride(CommandSourceStack source, double x, double y, double z) {
		final ServerLevel level = source.getLevel();
		final WindManager windManager = WindManager.getOrCreateWindManager(level);
		windManager.overrideWind = true;
		windManager.windX = x;
		windManager.windY = y;
		windManager.windZ = z;
		windManager.windOverride = Optional.of(new Vec3(x, y, z));
		windManager.sendSync(level);
		source.sendSuccess(() -> Component.translatable("commands.wind.success", x, y, z), true);
		return 1;
	}

	private static int displayWindValue(CommandSourceStack source, boolean atPos) {
		final ServerLevel level = source.getLevel();
		final WindManager windManager = WindManager.getOrCreateWindManager(level);
		if (!atPos) {
			source.sendSuccess(
				() -> Component.translatable("commands.wind.display.global.success", windManager.windX, windManager.windY, windManager.windZ),
				true
			);
		} else {
			Vec3 sourcePos = source.getPosition();
			source.sendSuccess(
				() -> Component.translatable(
					"commands.wind.display.pos.success",
					sourcePos.x, sourcePos.y, sourcePos.z,
					windManager.windX, windManager.windY, windManager.windZ
				),
				true
			);
		}
		return 1;
	}

}
