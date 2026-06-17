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

package net.frozenblock.lib.entity.client.api.spottingicon.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcon;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import java.util.Collection;

@ApiStatus.Internal
public final class SpottingIconCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubcommand() {
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
						.then(Commands.argument("faderStartDist", FloatArgumentType.floatArg())
							.then(Commands.argument("faderEndDist", FloatArgumentType.floatArg())
								.then(Commands.argument("faderStartVal", FloatArgumentType.floatArg(0F, 1F))
									.then(Commands.argument("faderEndVal", FloatArgumentType.floatArg(0F, 1F))
										.executes(ctx -> addIconToEntities(
											ctx.getSource(),
											EntityArgument.getEntities(ctx, "targets"),
											SpottingIcon.builder()
												.texture(toTextureIdentifier(IdentifierArgument.getId(ctx, "texture")))
												.fader(
													FloatArgumentType.getFloat(ctx, "faderStartDist"),
													FloatArgumentType.getFloat(ctx, "faderEndDist"),
													FloatArgumentType.getFloat(ctx, "faderStartVal"),
													FloatArgumentType.getFloat(ctx, "faderEndVal")
												)
												.build()
										))
										.then(Commands.argument("scalerStartDist", FloatArgumentType.floatArg())
											.then(Commands.argument("scalerEndDist", FloatArgumentType.floatArg())
												.then(Commands.argument("scalerStartVal", FloatArgumentType.floatArg())
													.then(Commands.argument("scalerEndVal", FloatArgumentType.floatArg())
														.executes(ctx -> addIconToEntities(
															ctx.getSource(),
															EntityArgument.getEntities(ctx, "targets"),
															SpottingIcon.builder()
																.texture(toTextureIdentifier(IdentifierArgument.getId(ctx, "texture")))
																.fader(
																	FloatArgumentType.getFloat(ctx, "faderStartDist"),
																	FloatArgumentType.getFloat(ctx, "faderEndDist"),
																	FloatArgumentType.getFloat(ctx, "faderStartVal"),
																	FloatArgumentType.getFloat(ctx, "faderEndVal")
																)
																.scaler(
																	FloatArgumentType.getFloat(ctx, "scalerStartDist"),
																	FloatArgumentType.getFloat(ctx, "scalerEndDist"),
																	FloatArgumentType.getFloat(ctx, "scalerStartVal"),
																	FloatArgumentType.getFloat(ctx, "scalerEndVal")
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
						.suggests((ctx, builder) -> {
							try {
								Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "targets");
								return SharedSuggestionProvider.suggestResource(
									entities.stream()
										.flatMap(e -> SpottingIcons.get(e).icons().stream())
										.map(icon -> fromTextureIdentifier(icon.texture()))
										.distinct()
										.toList(),
									builder
								);
							} catch (Exception ignored) {
								return builder.buildFuture();
							}
						})
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
