/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
			if (entity instanceof LivingEntity livingEntity) {
				AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.SCALE);
				if (attributeInstance != null) {
					attributeInstance.setBaseValue(scale);
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
