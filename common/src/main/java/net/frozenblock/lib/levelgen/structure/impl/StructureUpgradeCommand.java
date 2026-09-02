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

package net.frozenblock.lib.levelgen.structure.impl;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import net.frozenblock.lib.file.nbt.NbtFileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.SharedConstants;

public class StructureUpgradeCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> buildSubCommand() {
		return Commands.literal("structure_upgrade")
			.then(
				Commands.argument("namespace", StringArgumentType.string())
					.executes(
						context -> upgradeAndExportPieces(
							context.getSource(),
							StringArgumentType.getString(context, "namespace"),
							false
						)
					)
					.then(
						Commands.argument("log", BoolArgumentType.bool())
							.executes(
								context -> upgradeAndExportPieces(
									context.getSource(),
									StringArgumentType.getString(context, "namespace"),
									BoolArgumentType.getBool(context, "log")
								)
							)
					)
			);
	}

	private static int upgradeAndExportPieces(CommandSourceStack source, String namespace, boolean log) {
		final ResourceManager resourceManager = source.getServer().getResourceManager();

		final Set<Identifier> foundPieces = resourceManager.listResources(
			"structure",
			identifier -> identifier.getPath().endsWith(".nbt") && identifier.getNamespace().equals(namespace)
		).keySet();

		if (log) foundPieces.forEach(identifier -> System.out.println("Found piece: " + identifier.toString()));

		final StructureTemplateManager structureTemplateManager = source.getLevel().getStructureTemplateManager();
		final Map<Identifier, CompoundTag> savedTemplates = new Object2ObjectLinkedOpenHashMap<>();

		foundPieces.forEach((identifier) -> {
			try {
				final Identifier cleanedId = identifier.withPath(path -> path.replaceFirst("structure/", "").replace(".nbt", ""));
				structureTemplateManager.get(cleanedId).ifPresent(structureTemplate -> savedTemplates.put(cleanedId, structureTemplate.save(new CompoundTag())));
			} catch (NoSuchElementException e) {
				throw new RuntimeException(e);
			}
		});

		final Path outputPath = source.getServer().getServerDirectory()
			.resolve("upgraded_structure/data_version_" + SharedConstants.getCurrentVersion().dataVersion().version());

		savedTemplates.forEach((identifier, compoundTag) -> {
			NbtFileUtils.saveToFile(
				compoundTag,
				outputPath.resolve(identifier.getNamespace()).toFile(),
				identifier.getPath().replace(".nbt", "")
			);
		});

		final int templateCount = savedTemplates.size();
		if (templateCount > 0) {
			source.sendSuccess(() -> Component.translatable("commands.structure_upgrade.success", templateCount, namespace), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.structure_upgrade.failure", namespace), true);
		}
		return 1;
	}
}
