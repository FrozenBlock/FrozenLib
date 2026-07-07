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
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.DelayedRegistry;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.FabricDelayedRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.command.FrozenLibCommand;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateType;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcons;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.event.api.events.RegistryFreezeEvents;
import net.frozenblock.lib.block.api.sound.SoundTypeOverrides;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.event.impl.FabricEventBridge;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.frozenblock.lib.item.impl.FabricFuelRegistry;
import net.frozenblock.lib.item.impl.loot.predicates.FrozenLibLootConditionTypes;
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
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundTypes;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class FrozenLibFabric extends FrozenModInitializer {

	public FrozenLibFabric() {
		super(FrozenLibConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		DelayedRegistry.setFactory(FabricDelayedRegistry::new);
		FrozenLibMain.preQuiltInit();
		FrozenLibRegistries.init();
		SoundTypeOverrides.init();
		FabricEventBridge.initModStage();

		// QUILT INIT
		FrozenLibMain.quiltInit();

		// CONTINUE FROZENLIB INIT
		FrozenLibMain.init();
		FabricFuelRegistry.init();

		FrozenMainEntrypoint.EVENT.invoker().init(); // includes dev init

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			FrozenLibCommand.register(dispatcher);
		});

		ServerTickEvents.START_LEVEL_TICK.register(StructureStatusUpdater::updatePlayerStructureStatusesForLevel);
	}
}
