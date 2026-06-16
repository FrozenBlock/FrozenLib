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

package net.frozenblock.lib.screenshake.api.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.frozenblock.lib.screenshake.api.ScreenShake;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ScreenShakeCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("screenshake")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

		builder.then(Commands.argument("position", Vec3Argument.vec3()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "position"), ScreenShake.DEFAULT_INTENSITY, ScreenShake.DEFAULT_DURATION, ScreenShake.DEFAULT_FALLOFF_START_DURATION, ScreenShake.DEFAULT_MAX_DISTANCE))
			.then(Commands.argument("intensity", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "position"), FloatArgumentType.getFloat(context, "intensity"), ScreenShake.DEFAULT_DURATION, ScreenShake.DEFAULT_FALLOFF_START_DURATION, ScreenShake.DEFAULT_MAX_DISTANCE))
				.then(Commands.argument("duration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "position"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), 5, ScreenShake.DEFAULT_MAX_DISTANCE))
					.then(Commands.argument("falloffStartDuration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "position"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "falloffStartDuration"), ScreenShake.DEFAULT_MAX_DISTANCE))
						.then(Commands.argument("maxDistance", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), Vec3Argument.getVec3(context, "position"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "falloffStartDuration"), FloatArgumentType.getFloat(context, "maxDistance"))))))));

		builder.then(Commands.argument("targets", EntityArgument.entities()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "targets"), ScreenShake.DEFAULT_INTENSITY, ScreenShake.DEFAULT_DURATION, ScreenShake.DEFAULT_FALLOFF_START_DURATION, ScreenShake.DEFAULT_MAX_DISTANCE))
			.then(Commands.argument("intensity", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "targets"), FloatArgumentType.getFloat(context, "intensity"), ScreenShake.DEFAULT_DURATION, ScreenShake.DEFAULT_FALLOFF_START_DURATION, ScreenShake.DEFAULT_MAX_DISTANCE))
				.then(Commands.argument("duration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "targets"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), ScreenShake.DEFAULT_FALLOFF_START_DURATION, ScreenShake.DEFAULT_MAX_DISTANCE))
					.then(Commands.argument("falloffStartDuration", IntegerArgumentType.integer()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "targets"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "falloffStartDuration"), ScreenShake.DEFAULT_MAX_DISTANCE))
						.then(Commands.argument("maxDistance", FloatArgumentType.floatArg()).executes(context -> shake(context.getSource(), EntityArgument.getEntities(context, "targets"), FloatArgumentType.getFloat(context, "intensity"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "falloffStartDuration"), FloatArgumentType.getFloat(context, "maxDistance"))))))));

		builder.then(Commands.literal("remove")
			.then(Commands.literal("world").executes(context -> removeLevelScreenShakes(context.getSource())))
			.then(Commands.argument("targets", EntityArgument.entities()).executes(context -> removeEntityScreenShakes(context.getSource(), EntityArgument.getEntities(context, "targets"))))
		);

		return builder;
	}

	private static int shake(CommandSourceStack source, Vec3 vec3, float intensity, int duration, int falloffStartDuration, float maxDistance) {
		vec3 = new Vec3(Math.round(vec3.x()), Math.round(vec3.y()), Math.round(vec3.z()));
		ScreenShakes.add(
			source.getLevel(),
			ScreenShake.builder(source.getLevel(), vec3)
				.intensity(intensity)
				.duration(duration)
				.falloffStartDuration(falloffStartDuration)
				.maxDistance(maxDistance)
				.build()
		);
		final Vec3 finalVec = vec3;
		source.sendSuccess(() -> Component.translatable(
				"commands.screenshake.success",
				finalVec.x(),
				finalVec.y(),
				finalVec.z(),
				intensity,
				duration,
				falloffStartDuration,
				maxDistance
			),
			true
		);
		return 1;
	}

	private static int shake(CommandSourceStack source, Collection<? extends Entity> entities, float intensity, int duration, int falloffStartDuration, float maxDistance) {
		for (Entity entity : entities) {
			ScreenShakes.add(
				entity,
				ScreenShake.builder(entity)
					.intensity(intensity)
					.duration(duration)
					.falloffStartDuration(falloffStartDuration)
					.maxDistance(maxDistance)
					.build()
			);
		}

		final int entityCount = entities.size();
		final boolean oneEntity = entities.size() == 1;
		source.sendSuccess(() ->
				Component.translatable(
					oneEntity ? "commands.screenshake.entity.success" : "commands.screenshake.entity.success.multiple",
					oneEntity ? entities.stream().findFirst().get().getDisplayName() : entityCount,
					intensity,
					duration,
					falloffStartDuration,
					maxDistance
				),
			true
		);
		return entityCount;
	}

	private static int removeLevelScreenShakes(CommandSourceStack source) {
		final Level level = source.getLevel();
		final ScreenShakes screenShakes = ScreenShakes.get(level);
		if (screenShakes.isEmpty()) {
			source.sendFailure(Component.translatable("commands.screenshake.remove.level.fail"));
			return 0;
		}

		ScreenShakes.removeAttachment(level);

		final int screenShakeCount = screenShakes.screenShakes().size();
		final boolean oneScreenShake = screenShakeCount == 1;
		source.sendSuccess(() ->
				Component.translatable(
					oneScreenShake ? "commands.screenshake.remove.level.success" : "commands.screenshake.remove.level.success.multiple",
					oneScreenShake ? "" : screenShakeCount
				),
			true
		);
		return screenShakeCount;
	}

	private static int removeEntityScreenShakes(CommandSourceStack source, Collection<? extends Entity> entities) {
		final List<Entity> affectedEntities = new ArrayList<>();
		for (Entity entity : entities) {
			final ScreenShakes screenShakes = ScreenShakes.get(entity);
			if (screenShakes.isEmpty()) continue;

			affectedEntities.add(entity);
			ScreenShakes.removeAttachment(entity);
		}

		final int entityCount = affectedEntities.size();
		final boolean oneEntity = entityCount == 1;
		if (entityCount > 0) {
			source.sendSuccess(() ->
				Component.translatable(
					oneEntity ? "commands.screenshake.remove.entity.success" : "commands.screenshake.remove.entity.success.multiple",
					oneEntity ? entities.stream().findFirst().get().getDisplayName() : entityCount
				),
				true
			);
			return entityCount;
		}
		source.sendFailure(Component.translatable("commands.screenshake.remove.entity.failure"));
		return 0;
	}

}
