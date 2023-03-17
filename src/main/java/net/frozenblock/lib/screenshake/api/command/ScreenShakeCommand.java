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

package net.frozenblock.lib.screenshake.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.Collection;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ScreenShakeCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("screenshake").requires(source -> source.hasPermission(2))
				.then(Commands.argument("pos", Vec3Argument.vec3()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), 1F, 10, 5, 16F))
						.then(Commands.argument("intensity", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "intensity"), 10, 5, 16F))
								.then(Commands.argument("duration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), 5, 16F))
										.then(Commands.argument("durationFalloffStart", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "durationFalloffStart"), 16F))
												.then(Commands.argument("maxDistance", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "durationFalloffStart"), FloatArgumentType.getFloat(context, "maxDistance")))
														.then(Commands.argument("players", EntityArgument.players()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "durationFalloffStart"), FloatArgumentType.getFloat(context, "maxDistance"), EntityArgument.getPlayers(context, "players")))))))))
				.then(Commands.argument("entity", EntityArgument.entities()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "entity"), 1F, 10, 5, 16F))
						.then(Commands.argument("intensity", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "entity"), FloatArgumentType.getFloat(context, "intensity"), 10, 5, 16F))
								.then(Commands.argument("duration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "entity"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), 5, 16F))
										.then(Commands.argument("durationFalloffStart", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "entity"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "durationFalloffStart"), 16F))
												.then(Commands.argument("maxDistance", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "entity"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "durationFalloffStart"), FloatArgumentType.getFloat(context, "maxDistance"))))))))
		);
	}

	private static int shake(CommandSourceStack source, Vec3 vec3, float intensity, int duration, int durationFalloffStart, float maxDistance) {
		vec3 = new Vec3(Math.round(vec3.x()), Math.round(vec3.y()), Math.round(vec3.z()));
		ScreenShakeManager.addScreenShake(source.getLevel(), intensity, duration, durationFalloffStart, vec3.x(), vec3.y(), vec3.z(), maxDistance);
		source.sendSuccess(Component.translatable("commands.screenshake.success", vec3.x(), vec3.y(), vec3.z(), intensity, duration, durationFalloffStart, maxDistance), true);
		return 1;
	}

	private static int shake(CommandSourceStack source, Vec3 vec3, float intensity, int duration, int durationFalloffStart, float maxDistance, Collection<? extends ServerPlayer> entities) {
		vec3 = new Vec3(Math.round(vec3.x()), Math.round(vec3.y()), Math.round(vec3.z()));
		FriendlyByteBuf screenShakeByteBuf = ScreenShakeManager.createScreenShakeByteBuf(intensity, duration, durationFalloffStart, vec3.x(), vec3.y(), vec3.z(), maxDistance, 0);
		StringBuilder playerString = new StringBuilder();
		for (ServerPlayer serverPlayer : entities) {
			ScreenShakeManager.addScreenShake(source.getLevel(), intensity, duration, durationFalloffStart, vec3.x(), vec3.y(), vec3.z(), maxDistance);
			playerString.append(serverPlayer.getDisplayName()).append(", ");
		}
		source.sendSuccess(Component.translatable("commands.screenshake.player.success", playerString.toString(), vec3.x(), vec3.y(), vec3.z(), intensity, duration, durationFalloffStart, maxDistance), true);

		return 1;
	}

	private static int shake(CommandSourceStack source, Collection<? extends Entity> entities, float intensity, int duration, int durationFalloffStart, float maxDistance) {
		StringBuilder entityString = new StringBuilder();
		for (Entity entity : entities) {
			ScreenShakeManager.addEntityScreenShake(entity, intensity, duration, durationFalloffStart, maxDistance);
			entityString.append(entity.getDisplayName()).append(", ");
		}
		source.sendSuccess(Component.translatable("commands.screenshake.entity.success", entityString.toString(), intensity, duration, durationFalloffStart, maxDistance), true);
		return 1;
	}

}