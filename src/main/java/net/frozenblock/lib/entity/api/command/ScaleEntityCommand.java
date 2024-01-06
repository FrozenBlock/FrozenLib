/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.entity.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class ScaleEntityCommand {

	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("scale").requires(source -> source.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.entities())
					.then(Commands.argument("scale", DoubleArgumentType.doubleArg())
						.executes(context -> scale(context.getSource(), EntityArgument.getEntities(context, "targets"), DoubleArgumentType.getDouble(context, "scale")))
					)
				)
		);
	}

	private static int scale(CommandSourceStack source, @NotNull Collection<? extends Entity> entities, double scale) {
		StringBuilder entityString = new StringBuilder();
		int entityAmount = 0;
		List<Entity> affectedEntities = new ArrayList<>();
		for (Entity entity : entities) {
			if (entity instanceof LivingEntity livingEntity && livingEntity.getAttributes() != null) {
				AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.SCALE);
				if (attributeInstance != null) {
					attributeInstance.setBaseValue(scale);
					entityString.append(entity.getDisplayName().getString()).append(", ");
					entityAmount += 1;
					affectedEntities.add(livingEntity);
				}
			}
		}

		boolean oneEntity = affectedEntities.size() == 1;
		for (Entity entity : affectedEntities) {
			entityString.append(entity.getDisplayName().getString()).append(oneEntity ? "" : ", ");
		}

		if (entityAmount > 0) {
			source.sendSuccess(() -> Component.translatable(oneEntity ? "commands.scale.entity.success" : "commands.scale.entity.success.multiple", entityString.toString(), scale), true);
			return 1;
		} else {
			source.sendFailure(Component.translatable("commands.scale.entity.failure"));
			return 0;
		}
	}

}
