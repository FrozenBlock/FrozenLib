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

package net.frozenblock.lib.tag.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.tag.api.TagArgument;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

@UtilityClass
public final class TagListCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("taglist")
			.requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS));

		addRegistryArgument(builder, Registries.BIOME, "biome");
		addRegistryArgument(builder, Registries.BLOCK, "block");
		addRegistryArgument(builder, Registries.BLOCK_SOUND_SET, "block_sound_set");
		addRegistryArgument(builder, Registries.DAMAGE_TYPE, "damage_type");
		addRegistryArgument(builder, Registries.ENCHANTMENT, "enchantment");
		addRegistryArgument(builder, Registries.ENTITY_TYPE, "entity_type");
		addRegistryArgument(builder, Registries.FEATURE, "feature");
		addRegistryArgument(builder, Registries.FLUID, "fluid");
		addRegistryArgument(builder, Registries.INSTRUMENT, "instrument");
		addRegistryArgument(builder, Registries.ITEM, "item");
		addRegistryArgument(builder, Registries.POINT_OF_INTEREST_TYPE, "point_of_interest_type");
		addRegistryArgument(builder, Registries.STRUCTURE, "structure");
		addRegistryArgument(builder, Registries.TIMELINE, "timeline");
		addRegistryArgument(builder, Registries.VILLAGER_TRADE, "villager_trade");

		return builder;
	}

	private static <T> void addRegistryArgument(
		LiteralArgumentBuilder<CommandSourceStack> builder,
		ResourceKey<Registry<T>> registryKey,
		String literal
	) {
		builder.then(Commands.literal(literal)
			.then(Commands.argument(literal, TagArgument.tag(registryKey))
				.executes(
					context -> list(
						context.getSource(),
						TagArgument.getTag(context, literal, registryKey)
					)
				)
			)
		);
	}

	private static <T> int list(CommandSourceStack source, HolderSet.Named<T> holderSet) {
		for (Holder<T> holder : holderSet) {
			if (!holderSet.contains(holder)) continue;
			source.sendSuccess(
				() -> Component.literal(holder.unwrapKey().orElseThrow().identifier().toString()),
				true
			);
		}

		final int size = holderSet.size();
		source.sendSuccess(
			() -> Component.translatable("commands.frozenlib.taglist.footer", size, holderSet.key()),
			true
		);
		return size;
	}
}
