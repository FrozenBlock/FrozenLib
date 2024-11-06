/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.structure.impl.upgrade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StructureUpgradeCommand {
	private StructureUpgradeCommand() {}

	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("structure")
			.then(Commands.literal("snbt")
				.then(Commands.literal("export")
					.then(Commands.argument("namespace", StringArgumentType.string())
						.executes(context -> exportPiecesToSNBT(context.getSource(), StringArgumentType.getString(context, "namespace")))
					)
				)
				.then(Commands.literal("upgrade")
					.then(Commands.argument("namespace", StringArgumentType.string())
						.executes(context -> exportSNBTToNBT(context.getSource(), StringArgumentType.getString(context, "namespace")))
					)
				)
			)
			.then(Commands.literal("piece")
				.then(Commands.literal("count")
						.then(Commands.argument("namespace", StringArgumentType.string())
							.executes(context -> countPieces(context.getSource(), StringArgumentType.getString(context, "namespace")))
						)
				)
			)
		);
	}

	private static int exportPiecesToSNBT(@NotNull CommandSourceStack source, String namespace) {
		try {
			ResourceManager resourceManager = source.getServer().getResourceManager();

			Set<ResourceLocation> foundPieces = resourceManager.listResources(
				"structure",
				resourceLocation -> resourceLocation.getPath().endsWith(".nbt") && resourceLocation.getNamespace().equals(namespace)
			).keySet();

			Path path = source.getServer().getServerDirectory();
			if (path.getParent() != null) path = path.getParent();
			path = path.resolve("structure");
			Path inputPath = path.resolve("nbt_input");
			Path outputPath = path.resolve("snbt");
			outputPath.toFile().delete();
			outputPath.toFile().mkdirs();

			for (ResourceLocation resourceLocation : foundPieces) {
				String putPath = resourceLocation.getNamespace() + "/" + resourceLocation.getPath().replaceFirst("structure/", "");
				Path exportedPath = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, inputPath.resolve(putPath), putPath.replace(".nbt", ""), outputPath);
				if (exportedPath != null) {
					try {
						FileUtil.createDirectoriesSafe(exportedPath.getParent());
					} catch (IOException var7) {
						FrozenSharedConstants.LOGGER.error("Could not create export folder", var7);
					}
				} else {
					FrozenSharedConstants.LOGGER.error("Failed to export {}", resourceLocation);
				}
			}
			long streamCount = Files.walk(outputPath).filter(p -> p.toString().endsWith(".snbt")).count();

			if (streamCount > 0) {
				source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_snbt.success", streamCount, foundPieces.size(), namespace), true);
			} else {
				source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_snbt.failure", namespace), true);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int exportSNBTToNBT(@NotNull CommandSourceStack source, String namespace) {
		try {
			ResourceManager resourceManager = source.getServer().getResourceManager();

			Map<ResourceLocation, Resource> foundPieces = resourceManager.listResources(
				"structure",
				resourceLocation -> resourceLocation.getPath().endsWith(".nbt") && resourceLocation.getNamespace().equals(namespace)
			);

			Path path = source.getServer().getServerDirectory();
			if (path.getParent() != null) path = path.getParent();
			path = path.resolve("structure");
			Path inputPath = path.resolve("snbt");
			Path outputPath = path.resolve("nbt_output");
			outputPath.toFile().delete();
			outputPath.toFile().mkdirs();

			PackOutput packOutput = new PackOutput(outputPath);
			SnbtToNbt snbtToNbt = new SnbtToNbt(packOutput, Set.of(inputPath)).addFilter(new CommandStructureUpdater());
			CompletableFuture completableFuture = snbtToNbt.run(CachedOutput.NO_CACHE);

			while (!completableFuture.isDone()) {
			}

			long streamCount = Files.walk(outputPath).filter(p -> p.toString().endsWith(".nbt")).count();

			if (streamCount > 0) {
				source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_upgrade.success", streamCount, foundPieces.size(), namespace), true);
			} else {
				source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_upgrade.failure", namespace), true);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int countPieces(@NotNull CommandSourceStack source, String namespace) {
		ResourceManager resourceManager = source.getServer().getResourceManager();

		Set<ResourceLocation> foundPieces = resourceManager.listResources(
			"structure",
			resourceLocation -> resourceLocation.getPath().endsWith(".nbt") && resourceLocation.getNamespace().equals(namespace)
		).keySet();

		int pieceCount = foundPieces.size();
		if (pieceCount > 0) {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_piece_count.success", pieceCount, namespace), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.frozenlib.structure_piece_count.failure", namespace), true);
		}
		return 1;
	}
}
