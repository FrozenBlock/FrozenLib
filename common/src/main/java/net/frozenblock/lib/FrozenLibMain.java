/*
 * Copyright (C) 2026 FrozenBlock
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

import net.frozenblock.lib.block.api.sound.SoundTypeOverrides;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateType;
import net.frozenblock.lib.entity.api.cubemob.sulfurcube.SulfurCubeEvents;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcons;
import net.frozenblock.lib.entity.impl.variant.FrozenLibSpawnConditions;
import net.frozenblock.lib.event.api.events.RegistryFreezeEvents;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldowns;
import net.frozenblock.lib.item.impl.loot.predicates.FrozenLibLootConditionTypes;
import net.frozenblock.lib.levelgen.attribute.api.FrozenLibEnvironmentAttributes;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeModificationImpl;
import net.frozenblock.lib.levelgen.feature.api.FrozenLibFeatures;
import net.frozenblock.lib.levelgen.feature.impl.blockpredicates.FrozenLibBlockPredicateTypes;
import net.frozenblock.lib.levelgen.placement.impl.FrozenLibPlacementModifiers;
import net.frozenblock.lib.levelgen.structure.api.placement.StructureGenerationConditionApi;
import net.frozenblock.lib.levelgen.structure.api.placement.StructurePlacementExclusionApi;
import net.frozenblock.lib.levelgen.structure.api.pools.TemplatePoolApi;
import net.frozenblock.lib.levelgen.structure.impl.processor.FrozenLibRuleBlockEntityModifiers;
import net.frozenblock.lib.levelgen.structure.impl.pools.FrozenLibStructurePoolElementTypes;
import net.frozenblock.lib.levelgen.structure.impl.processor.FrozenLibStructureProcessorTypes;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatusUpdater;
import net.frozenblock.lib.levelgen.surface.impl.ConfigConditionSource;
import net.frozenblock.lib.levelgen.surface.impl.SurfaceRuleUtil;
import net.frozenblock.lib.networking.impl.FrozenLibNetworking;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundTypes;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;

public final class FrozenLibMain {

	public static void preQuiltSetup() {
		FireData.init();
		SerializableItemCooldowns.init();
		SoundTypeOverrides.init();
	}

	public static void quiltSetup() {
		ServerFreezer.onInitialize();
		ModProtocol.loadVersions();
		ServerRegistrySync.registerHandlers();
	}

	public static void setup() {
		FrozenDeferredRegister<ArgumentTypeInfo<?, ?>> argTypes = FrozenDeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, FrozenLibConstants.MOD_ID);

		argTypes.register(
			"tag_key",
			() -> new TagKeyArgument.Info<>(),
			info -> ArgumentTypeInfos.BY_CLASS.put(
				ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
				info
			)
		);

		argTypes.register();

		CapeUtil.init();
		SpottingIcons.init();
		SulfurCubeEvents.init();
		StructureStatus.init();
		SoundPredicate.init();
		MovingSoundTypes.init();
		FrozenLibParticleTypes.init();
		FrozenLibEnvironmentAttributes.init();
		FrozenLibRuleBlockEntityModifiers.init();
		FrozenLibStructureProcessorTypes.init();
		FrozenLibStructurePoolElementTypes.init();
		FrozenLibDataComponents.init();
		FrozenLibFeatures.init();
		ConfigPredicateType.init();
		FrozenLibSpawnConditions.init();
		WindManager.init();
		WindManagerExtensionType.init();
		WindDisturbances.init();
		WindDisturbanceType.init();
		FrozenLibBlockPredicateTypes.init();
		FrozenLibPlacementModifiers.init();
		FrozenLibLootConditionTypes.init();
		SurfaceRuleUtil.init();
		BiomeModificationImpl.init();
		StructureGenerationConditionApi.init();
		StructurePlacementExclusionApi.init();
		TemplatePoolApi.init();

		var matCon = FrozenDeferredRegister.create(
			Registries.MATERIAL_CONDITION,
			FrozenLibConstants.MOD_ID
		);
		matCon.register("config_predicate", () -> ConfigConditionSource.CODEC);
		matCon.register();

		ScreenShakes.init();
		StructureStatusUpdater.init();

		FrozenLibNetworking.registerNetworking();

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
