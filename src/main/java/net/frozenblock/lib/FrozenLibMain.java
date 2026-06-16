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

package net.frozenblock.lib;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.command.FrozenLibCommand;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateType;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.event.api.RegistryFreezeEvents;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.frozenblock.lib.levelgen.feature.api.FrozenLibFeatures;
import net.frozenblock.lib.levelgen.feature.impl.blockpredicates.FrozenLibBlockPredicateTypes;
import net.frozenblock.lib.levelgen.placement.impl.FrozenLibPlacementModifiers;
import net.frozenblock.lib.levelgen.structure.api.StructureGenerationConditionApi;
import net.frozenblock.lib.levelgen.structure.api.StructurePlacementExclusionApi;
import net.frozenblock.lib.levelgen.structure.api.TemplatePoolApi;
import net.frozenblock.lib.levelgen.structure.impl.FrozenLibRuleBlockEntityModifiers;
import net.frozenblock.lib.levelgen.structure.impl.FrozenLibStructurePoolElementTypes;
import net.frozenblock.lib.levelgen.structure.impl.FrozenLibStructureProcessorTypes;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatusUpdater;
import net.frozenblock.lib.levelgen.surface.impl.ConfigConditionSource;
import net.frozenblock.lib.loot.impl.predicates.FrozenLibLootConditionTypes;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundTypes;
import net.frozenblock.lib.sound.impl.MovingSoundManager;
import net.frozenblock.lib.spottingicon.api.SpottingIcons;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.v2.WindManager;
import net.frozenblock.lib.wind.v2.extension.WindManagerExtensionType;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;

public final class FrozenLibMain extends FrozenModInitializer {

	public FrozenLibMain() {
		super(FrozenLibConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		FrozenLibRegistries.init();

		// QUILT INIT
		ServerFreezer.onInitialize();
		ModProtocol.loadVersions();
		ServerRegistrySync.registerHandlers();

		// CONTINUE FROZENLIB INIT
		FrozenLibRuleBlockEntityModifiers.init();
		FrozenLibStructureProcessorTypes.init();
		FrozenLibStructurePoolElementTypes.init();
		SoundPredicate.init();
		MovingSoundTypes.init();
		SpottingIcons.init();
		WindDisturbanceLogic.init();
		FrozenLibDataComponents.init();
		FrozenLibParticleTypes.init();
		FrozenLibFeatures.init();
		ConfigPredicateType.init();
		WindManagerExtensionType.init();
		FrozenLibBlockPredicateTypes.init();
		FrozenLibPlacementModifiers.init();
		FrozenLibLootConditionTypes.init();
		StructureGenerationConditionApi.init();
		StructurePlacementExclusionApi.init();
		TemplatePoolApi.init();

		Registry.register(BuiltInRegistries.MATERIAL_CONDITION, FrozenLibConstants.id("config_predicate"), ConfigConditionSource.CODEC);

		StructureStatus.init();
		CapeUtil.init();
		ScreenShakes.init();

		FrozenMainEntrypoint.EVENT.invoker().init(); // includes dev init

		ArgumentTypeInfos.register(
			BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
			FrozenLibConstants.string("tag_key"),
			ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
			new TagKeyArgument.Info<>()
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			FrozenLibCommand.register(dispatcher);
		});

		ServerLevelEvents.UNLOAD.register((server, serverLevel) -> {
			WindManager.getOrCreateWindManager(serverLevel).clearAllWindDisturbances();
		});

		ServerTickEvents.START_LEVEL_TICK.register(serverLevel -> {
			WindManager.getOrCreateWindManager(serverLevel).clearAndSwitchWindDisturbances();
			WindManager.getOrCreateWindManager(serverLevel).tick(serverLevel);
			StructureStatusUpdater.updatePlayerStructureStatusesForLevel(serverLevel);
			ScreenShakes.tick(serverLevel, serverLevel);
			for (Entity entity : serverLevel.getAllEntities()) {
				if (entity.isRemoved()) continue;
				ScreenShakes.tick(serverLevel, entity);
			}
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			MovingSoundManager.syncWithPlayer(entity, player);
		});

		FrozenNetworking.registerNetworking();

		RegistryFreezeEvents.START_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (allRegistries) ModIntegrations.initialize();
		});

		RegistryFreezeEvents.END_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (!allRegistries) return;
			for (Config<?> config : ConfigRegistry.getAllConfigs()) config.save();
		});

		FrozenLibConfig.CONFIG.load(true);
	}
}
