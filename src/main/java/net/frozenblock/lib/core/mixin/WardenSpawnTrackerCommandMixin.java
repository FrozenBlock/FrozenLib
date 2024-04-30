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

package net.frozenblock.lib.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WardenSpawnTrackerCommand.class)
public abstract class WardenSpawnTrackerCommandMixin {

	@Shadow
	private static int resetTracker(CommandSourceStack source, Collection<? extends Player> targets) {
		return 0;
	}

	@Shadow
	private static int setWarningLevel(CommandSourceStack source, Collection<? extends Player> targets, int warningLevel) {
		return 0;
	}

	@WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;", remap = false))
	private static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> dispatcher, LiteralArgumentBuilder<CommandSourceStack> builder, Operation<LiteralCommandNode<CommandSourceStack>> operation) {
		if (FrozenLibConfig.get().wardenSpawnTrackerCommand) {
			return dispatcher.register(
				Commands.literal("warden_spawn_tracker")
					.requires(player -> player.hasPermission(2))
					.then(
						Commands.literal("clear")
							.executes(context -> resetTracker(context.getSource(), List.of(context.getSource().getPlayerOrException())))
							.then(
								Commands.argument("targets", EntityArgument.players()).executes(context -> resetTracker(context.getSource(), EntityArgument.getPlayers(context, "targets")))
							)
					)
					.then(
						Commands.literal("set")
							.then(
								Commands.argument("targets", EntityArgument.players())
									.then(
										Commands.argument("warning_level", IntegerArgumentType.integer(0, 4))
											.executes(
												context -> setWarningLevel(
													context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "warning_level")
												)
											)
									)
							)
					)
			);
		} else return operation.call(dispatcher, builder);
	}

	@WrapOperation(method = "setWarningLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;sendSuccess(Ljava/util/function/Supplier;Z)V", ordinal = 0))
	private static void modifySetWarningLevel(CommandSourceStack source, Supplier<Component> supplier, boolean broadcastToOps, Operation<Void> operation, CommandSourceStack source1, Collection<? extends Player> targets, int warningLevel) {
		if (FrozenLibConfig.get().wardenSpawnTrackerCommand) {
			source.sendSuccess(
				() -> Component.translatable("commands.warden_spawn_tracker.set.success.single", warningLevel, targets.iterator().next().getDisplayName()), true
			);
		} else operation.call(source, supplier, broadcastToOps);
	}

	@WrapOperation(method = "setWarningLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;sendSuccess(Ljava/util/function/Supplier;Z)V", ordinal = 1))
	private static void modifySetWarningLevelMultiple(CommandSourceStack source, Supplier<Component> supplier, boolean broadcastToOps, Operation<Void> operation, CommandSourceStack source1, Collection<? extends Player> targets, int warningLevel) {
		if (FrozenLibConfig.get().wardenSpawnTrackerCommand) {
			source.sendSuccess(
				() -> Component.translatable("commands.warden_spawn_tracker.set.success.multiple", warningLevel, targets.size()), true
			);
		} else operation.call(source, supplier, broadcastToOps);
	}
}
