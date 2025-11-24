/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.block.sound.impl.BlockSoundTypeManager;
import net.frozenblock.lib.cape.impl.ServerCapeData;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.core.impl.DataPackReloadMarker;
import net.frozenblock.lib.entity.api.command.ScaleEntityCommand;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.event.api.PlayerJoinEvents;
import net.frozenblock.lib.event.api.RegistryFreezeEvents;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.frozenblock.lib.screenshake.api.command.ScreenShakeCommand;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.frozenblock.lib.tag.api.TagListCommand;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.command.WindCommand;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatures;
import net.frozenblock.lib.worldgen.feature.api.block_predicate.FrozenLibBlockPredicateTypes;
import net.frozenblock.lib.worldgen.feature.api.placementmodifier.FrozenPlacementModifiers;
import net.frozenblock.lib.worldgen.structure.api.StructureGenerationConditionApi;
import net.frozenblock.lib.worldgen.structure.api.StructurePlacementExclusionApi;
import net.frozenblock.lib.worldgen.structure.api.TemplatePoolApi;
import net.frozenblock.lib.worldgen.structure.impl.FrozenRuleBlockEntityModifiers;
import net.frozenblock.lib.worldgen.structure.impl.FrozenStructurePoolElementTypes;
import net.frozenblock.lib.worldgen.structure.impl.FrozenStructureProcessorTypes;
import net.frozenblock.lib.worldgen.structure.impl.StructureUpgradeCommand;
import net.frozenblock.lib.worldgen.structure.impl.status.StructureStatusUpdater;
import net.frozenblock.lib.worldgen.surface.impl.BiomeTagConditionSource;
import net.frozenblock.lib.worldgen.surface.impl.OptimizedBiomeTagConditionSource;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.server.packs.PackType;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;

public final class FrozenLibMain extends FrozenModInitializer {

	public FrozenLibMain() {
		super(FrozenLibConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		FrozenLibRegistries.initRegistry();

		// QUILT INIT

		ServerFreezer.onInitialize();
		ModProtocol.loadVersions();
		ServerRegistrySync.registerHandlers();

		// CONTINUE FROZENLIB INIT

		FrozenRuleBlockEntityModifiers.init();
		FrozenStructureProcessorTypes.init();
		FrozenStructurePoolElementTypes.init();
		SoundPredicate.init();
		SpottingIconPredicate.init();
		WindDisturbanceLogic.init();
		FrozenLibParticleTypes.init();
		FrozenLibFeatures.init();
		FrozenLibBlockPredicateTypes.init();
		FrozenPlacementModifiers.init();
		DataPackReloadMarker.init();
		StructureGenerationConditionApi.init();
		StructurePlacementExclusionApi.init();
		TemplatePoolApi.init();

		Registry.register(BuiltInRegistries.MATERIAL_CONDITION, FrozenLibConstants.id("biome_tag_condition_source"), BiomeTagConditionSource.CODEC.codec());
		Registry.register(BuiltInRegistries.MATERIAL_CONDITION, FrozenLibConstants.id("optimized_biome_tag_condition_source"), OptimizedBiomeTagConditionSource.CODEC.codec());

		ServerCapeData.init();

		FrozenMainEntrypoint.EVENT.invoker().init(); // includes dev init

		ArgumentTypeInfos.register(
			BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
			FrozenLibConstants.string("tag_key"),
			ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
			new TagKeyArgument.Info<>()
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WindCommand.register(dispatcher);
			ScreenShakeCommand.register(dispatcher);
			ConfigCommand.register(dispatcher);
			TagListCommand.register(dispatcher);
			ScaleEntityCommand.register(dispatcher);
			StructureUpgradeCommand.register(dispatcher);
		});

		ServerWorldEvents.UNLOAD.register((server, serverLevel) -> {
			WindManager.getOrCreateWindManager(serverLevel).clearAllWindDisturbances();
		});

		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			WindManager.getOrCreateWindManager(serverLevel).clearAndSwitchWindDisturbances();
			WindManager.getOrCreateWindManager(serverLevel).tick(serverLevel);
			ScreenShakeManager.getOrCreateScreenShakeManager(serverLevel).tick(serverLevel);
			StructureStatusUpdater.updatePlayerStructureStatusesForLevel(serverLevel);
		});

		PlayerJoinEvents.ON_PLAYER_ADDED_TO_LEVEL.register(((server, serverLevel, player) -> {
			WindManager windManager = WindManager.getOrCreateWindManager(serverLevel);
			windManager.sendSyncToPlayer(windManager.createSyncPacket(), player);
		}));

		if (FrozenLibConfig.get().wardenSpawnTrackerCommand) {
			CommandRegistrationCallback.EVENT.register(
				((dispatcher, registryAccess, environment) -> WardenSpawnTrackerCommand.register(dispatcher))
			);
		}
		FrozenNetworking.registerNetworking();

		RegistryFreezeEvents.START_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (allRegistries) ModIntegrations.initialize();
		});

		RegistryFreezeEvents.END_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (!allRegistries) return;
			for (Config<?> config : ConfigRegistry.getAllConfigs()) config.save();
		});

		final var resourceLoader = ResourceManagerHelper.get(PackType.SERVER_DATA);
		resourceLoader.registerReloadListener(BlockSoundTypeManager.INSTANCE);
	}
}
