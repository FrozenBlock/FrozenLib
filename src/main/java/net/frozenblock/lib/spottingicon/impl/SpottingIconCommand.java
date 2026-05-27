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

package net.frozenblock.lib.spottingicon.impl;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.spottingicon.api.SpottingIconManager;
import net.frozenblock.lib.spottingicon.api.SpottingIconPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SpottingIconCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubcommand() {
		return Commands.literal("spotting")
			.then(Commands.literal("add")
				.then(Commands.argument("targets", EntityArgument.entities())
					.then(Commands.argument("texture", IdentifierArgument.id())
						.executes(ctx -> addIcon(
							ctx.getSource(),
							EntityArgument.getEntities(ctx, "targets"),
							IdentifierArgument.getId(ctx, "texture"),
							16F,
							20F,
							SpottingIconPredicate.DEFAULT_ID
						))
						.then(Commands.argument("startFade", FloatArgumentType.floatArg())
							.then(Commands.argument("endFade", FloatArgumentType.floatArg())
								.executes(ctx -> addIcon(
									ctx.getSource(),
									EntityArgument.getEntities(ctx, "targets"),
									IdentifierArgument.getId(ctx, "texture"),
									FloatArgumentType.getFloat(ctx, "startFade"),
									FloatArgumentType.getFloat(ctx, "endFade"),
									SpottingIconPredicate.DEFAULT_ID
								))
								.then(Commands.argument("predicate", IdentifierArgument.id())
									.suggests((ctx, builder) ->
										SharedSuggestionProvider.suggestResource(
											FrozenLibRegistries.SPOTTING_ICON_PREDICATE.keySet(),
											builder
										)
									)
									.executes(ctx -> addIcon(
										ctx.getSource(),
										EntityArgument.getEntities(ctx, "targets"),
										IdentifierArgument.getId(ctx, "texture"),
										FloatArgumentType.getFloat(ctx, "startFade"),
										FloatArgumentType.getFloat(ctx, "endFade"),
										IdentifierArgument.getId(ctx, "predicate")
									))
								)
							)
						)
					)
				)
			)
			.then(Commands.literal("remove")
				.then(Commands.argument("targets", EntityArgument.entities())
					.executes(ctx -> removeIcon(
						ctx.getSource(),
						EntityArgument.getEntities(ctx, "targets")
					))
				)
			);
	}

	private static Identifier toTextureIdentifier(Identifier arg) {
		return Identifier.fromNamespaceAndPath(arg.getNamespace(), "textures/spotting_icons/" + arg.getPath() + ".png");
	}

	private static int addIcon(
		CommandSourceStack source,
		Collection<? extends Entity> entities,
		Identifier textureArg,
		float startFade,
		float endFade,
		Identifier predicate
	) {
		final Identifier texture = toTextureIdentifier(textureArg);
		int count = 0;
		for (Entity entity : entities) {
			((EntitySpottingIconInterface) entity).frozenLib$getSpottingIconManager().setIcon(texture, startFade, endFade, predicate);
			count++;
		}
		if (count == 0) {
			source.sendFailure(Component.translatable("commands.frozenlib.spotting.add.failure"));
			return 0;
		}
		final int finalCount = count;
		if (count == 1) {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.spotting.add.success", finalCount), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.spotting.add.success.multiple", finalCount), true);
		}
		return count;
	}

	private static int removeIcon(CommandSourceStack source, Collection<? extends Entity> entities) {
		int count = 0;
		for (Entity entity : entities) {
			final SpottingIconManager manager = ((EntitySpottingIconInterface) entity).frozenLib$getSpottingIconManager();
			if (manager.icon != null) {
				manager.removeIcon();
				count++;
			}
		}
		if (count == 0) {
			source.sendFailure(Component.translatable("commands.frozenlib.spotting.remove.failure"));
			return 0;
		}
		final int finalCount = count;
		if (count == 1) {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.spotting.remove.success", finalCount), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.spotting.remove.success.multiple", finalCount), true);
		}
		return count;
	}
}
