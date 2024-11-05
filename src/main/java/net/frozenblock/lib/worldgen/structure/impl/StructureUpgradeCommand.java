package net.frozenblock.lib.worldgen.structure.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.frozenblock.lib.file.nbt.NbtFileUtils;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class StructureUpgradeCommand {
	private StructureUpgradeCommand() {}

	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("structure_upgrade")
			.then(Commands.argument("namespace", StringArgumentType.string())
				.executes(context -> upgradeAndExportPieces(context.getSource(), StringArgumentType.getString(context, "namespace"), false))
				.then(Commands.argument("log", BoolArgumentType.bool())
					.executes(context -> upgradeAndExportPieces(context.getSource(), StringArgumentType.getString(context, "namespace"), BoolArgumentType.getBool(context, "log")))
				)
			)
		);
	}

	private static int upgradeAndExportPieces(@NotNull CommandSourceStack source, String namespace, boolean log) {
		ResourceManager resourceManager = source.getServer().getResourceManager();

		Set<ResourceLocation> foundPieces = resourceManager.listResources(
			"structure",
			resourceLocation -> resourceLocation.getPath().endsWith(".nbt") && resourceLocation.getNamespace().equals(namespace)
		).keySet();

		if (log) {
			foundPieces.forEach(resourceLocation -> System.out.println("Found piece: " + resourceLocation.toString()));
		}

		StructureTemplateManager structureTemplateManager = source.getLevel().getStructureManager();
		Map<ResourceLocation, CompoundTag> savedTemplates = new Object2ObjectLinkedOpenHashMap<>();

		foundPieces.forEach((resourceLocation) -> {
			savedTemplates.put(resourceLocation, structureTemplateManager.getOrCreate(resourceLocation).save(new CompoundTag()));
		});

		Path outputPath = source.getServer().getServerDirectory()
			.resolve("upgraded_structure/data_version_" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());

		savedTemplates.forEach((resourceLocation, compoundTag) -> {
			NbtFileUtils.saveToFile(
				compoundTag,
				outputPath.resolve(resourceLocation.getNamespace()).toFile(),
				resourceLocation.getPath().replace(".nbt", "")
			);
		});

		int templateCount = savedTemplates.size();
		if (templateCount > 0) {
			source.sendSuccess(() -> Component.translatable("commands.structure_upgrade.success", templateCount, namespace), true);
		} else {
			source.sendSuccess(() -> Component.translatable("commands.structure_upgrade.failure", namespace), true);
		}
		return 1;
	}
}
