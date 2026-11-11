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

package net.frozenblock.lib.entity.api.spottingicon.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcon;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.FloatArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SpottingIconCommand {
	public static final SuggestionProvider<CommandSourceStack> SUGGEST_ENTITY_SPOTTING_ICONS = SuggestionProviders.serverOnly(
		(server, context, builder) -> {
			final Collection<? extends Entity> entities = EntityArgument.getEntities((CommandContext<CommandSourceStack>) (Object) context, "targets");
			return SuggestionContext.suggestResource(
				entities.stream()
					.flatMap(entity -> SpottingIcons.get(entity).icons().stream())
					.map(icon -> fromTextureIdentifier(icon.texture()))
					.distinct()
					.toList(),
				builder
			);
		}
	);

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		return Commands.literal("spotting")
			.then(Commands.literal("add")
				.then(Commands.argument("targets", EntityArgument.entities())
					.then(Commands.argument("texture", IdentifierArgument.id())
						.executes(ctx -> addIconToEntities(
							ctx.getSource(),
							EntityArgument.getEntities(ctx, "targets"),
							SpottingIcon.builder()
								.texture(toTextureIdentifier(IdentifierArgument.getId(ctx, "texture")))
								.build()
						))
						.then(Commands.argument("faderStartDist", FloatArgument.floatArg())
							.then(Commands.argument("faderEndDist", FloatArgument.floatArg())
								.then(Commands.argument("faderStartVal", FloatArgument.floatArg(0F, 1F))
									.then(Commands.argument("faderEndVal", FloatArgument.floatArg(0F, 1F))
										.executes(ctx -> addIconToEntities(
											ctx.getSource(),
											EntityArgument.getEntities(ctx, "targets"),
											SpottingIcon.builder()
												.texture(toTextureIdentifier(IdentifierArgument.getId(ctx, "texture")))
												.fader(
													FloatArgument.getFloat(ctx, "faderStartDist"),
													FloatArgument.getFloat(ctx, "faderEndDist"),
													FloatArgument.getFloat(ctx, "faderStartVal"),
													FloatArgument.getFloat(ctx, "faderEndVal")
												)
												.build()
										))
										.then(Commands.argument("scalerStartDist", FloatArgument.floatArg())
											.then(Commands.argument("scalerEndDist", FloatArgument.floatArg())
												.then(Commands.argument("scalerStartVal", FloatArgument.floatArg())
													.then(Commands.argument("scalerEndVal", FloatArgument.floatArg())
														.executes(ctx -> addIconToEntities(
															ctx.getSource(),
															EntityArgument.getEntities(ctx, "targets"),
															SpottingIcon.builder()
																.texture(toTextureIdentifier(IdentifierArgument.getId(ctx, "texture")))
																.fader(
																	FloatArgument.getFloat(ctx, "faderStartDist"),
																	FloatArgument.getFloat(ctx, "faderEndDist"),
																	FloatArgument.getFloat(ctx, "faderStartVal"),
																	FloatArgument.getFloat(ctx, "faderEndVal")
																)
																.scaler(
																	FloatArgument.getFloat(ctx, "scalerStartDist"),
																	FloatArgument.getFloat(ctx, "scalerEndDist"),
																	FloatArgument.getFloat(ctx, "scalerStartVal"),
																	FloatArgument.getFloat(ctx, "scalerEndVal")
																)
																.build()
														))
													)
												)
											)
										)
									)
								)
							)
						)
					)
				)
			)
			.then(Commands.literal("remove")
				.then(Commands.argument("targets", EntityArgument.entities())
					.executes(ctx -> removeAllIcons(
						ctx.getSource(),
						EntityArgument.getEntities(ctx, "targets")
					))
					.then(Commands.argument("texture", IdentifierArgument.id())
						.suggests(SUGGEST_ENTITY_SPOTTING_ICONS)
						.executes(ctx -> removeIcon(
							ctx.getSource(),
							EntityArgument.getEntities(ctx, "targets"),
							IdentifierArgument.getId(ctx, "texture")
						))
					)
				)
			);
	}

	private static Identifier toTextureIdentifier(Identifier arg) {
		return Identifier.fromNamespaceAndPath(arg.getNamespace(), "textures/spotting_icons/" + arg.getPath() + ".png");
	}

	private static Identifier fromTextureIdentifier(Identifier fullPath) {
		String path = fullPath.getPath();
		if (path.startsWith("textures/spotting_icons/") && path.endsWith(".png")) {
			path = path.substring("textures/spotting_icons/".length(), path.length() - ".png".length());
		}
		return Identifier.fromNamespaceAndPath(fullPath.getNamespace(), path);
	}

	private static int addIconToEntities(CommandSourceStack source, Collection<? extends Entity> entities, SpottingIcon icon) {
		int count = 0;
		for (Entity entity : entities) {
			SpottingIcons.add(entity, icon);
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

	private static int removeAllIcons(CommandSourceStack source, Collection<? extends Entity> entities) {
		int count = 0;
		for (Entity entity : entities) {
			if (SpottingIcons.has(entity)) {
				SpottingIcons.removeIf(entity, icon -> true);
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

	private static int removeIcon(CommandSourceStack source, Collection<? extends Entity> entities, Identifier textureArg) {
		final Identifier texture = toTextureIdentifier(textureArg);
		int count = 0;
		for (Entity entity : entities) {
			if (SpottingIcons.anyMatch(entity, icon -> icon.texture().equals(texture))) {
				SpottingIcons.removeIf(entity, icon -> icon.texture().equals(texture));
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
