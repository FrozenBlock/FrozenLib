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

import lombok.experimental.UtilityClass;
import net.fabricmc.frozenblock.datafixer.impl.ServerFreezer;
import net.frozenblock.lib.advancement.impl.predicates.entity.FrozenLibEntitySubPredicateTypes;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentEvents;
import net.frozenblock.lib.block.api.sound.BlockSoundSetOverrides;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.piston.PistonPushUtil;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.command.FrozenLibArgumentTypes;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v1.instance.BasicConfig;
import net.frozenblock.lib.config.v1.registry.BasicConfigRegistry;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateTypes;
import net.frozenblock.lib.entity.api.cubemob.sulfurcube.SulfurCubeEvents;
import net.frozenblock.lib.entity.api.spottingicon.SpottingIcons;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationData;
import net.frozenblock.lib.entity.impl.variant.FrozenLibSpawnConditions;
import net.frozenblock.lib.event.api.events.RegistryFreezeEvents;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.frozenblock.lib.item.impl.component.consume_effects.FrozenLibConsumeEffects;
import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldowns;
import net.frozenblock.lib.item.impl.loot.predicates.FrozenLibLootConditionTypes;
import net.frozenblock.lib.levelgen.attribute.api.FrozenLibEnvironmentAttributes;
import net.frozenblock.lib.levelgen.biome.api.attribute.BiomeEnvironmentAttributeModification;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeModificationImpl;
import net.frozenblock.lib.levelgen.blockpredicates.impl.FrozenLibBlockPredicateTypes;
import net.frozenblock.lib.levelgen.feature.impl.FrozenLibFeatureTypes;
import net.frozenblock.lib.levelgen.feature.impl.stateproviders.FrozenLibBlockStateProviderTypes;
import net.frozenblock.lib.levelgen.feature.impl.treedecorators.FrozenLibTreeDecoratorTypes;
import net.frozenblock.lib.levelgen.material.impl.FrozenLibMaterialConditionTypes;
import net.frozenblock.lib.levelgen.music.pitch.provider.PitchProviderTypes;
import net.frozenblock.lib.levelgen.placement.impl.FrozenLibPlacementModifierTypes;
import net.frozenblock.lib.levelgen.structure.api.StructureSetApi;
import net.frozenblock.lib.levelgen.structure.api.placement.StructureGenerationConditionApi;
import net.frozenblock.lib.levelgen.structure.api.placement.StructurePlacementExclusionApi;
import net.frozenblock.lib.levelgen.structure.api.pools.TemplatePoolApi;
import net.frozenblock.lib.levelgen.structure.api.processor.FrozenLibRuleBlockEntityModifiers;
import net.frozenblock.lib.levelgen.structure.impl.processor.FrozenLibStructureProcessorTypes;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatusUpdater;
import net.frozenblock.lib.networking.impl.FrozenLibNetworking;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundTypes;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibMain {

	public static void preQuiltInit() {
		BlockAttachmentEvents.init();
		FireData.init();
		SerializableItemCooldowns.init();
		BlockSoundSetOverrides.init();
		SuffocationData.init();
		BiomeEnvironmentAttributeModification.init();
	}

	public static void quiltInit() {
		ServerFreezer.onInitialize();
		ModProtocol.loadVersions();
		ServerRegistrySync.registerHandlers();
	}

	public static void init() {
		FrozenLibArgumentTypes.init();
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
		FrozenLibMaterialConditionTypes.init();
		FrozenLibDataComponents.init();
		FrozenLibConsumeEffects.init();
		FrozenLibFeatureTypes.init();
		FrozenLibTreeDecoratorTypes.init();
		FrozenLibBlockStateProviderTypes.init();
		ConfigPredicateTypes.init();
		PitchProviderTypes.init();
		FrozenLibSpawnConditions.init();
		FrozenLibEntitySubPredicateTypes.init();
		WindManager.init();
		WindManagerExtensionType.init();
		WindDisturbances.init();
		WindDisturbanceType.init();
		FrozenLibBlockPredicateTypes.init();
		FrozenLibPlacementModifierTypes.init();
		FrozenLibLootConditionTypes.init();
		BiomeModificationImpl.init();
		StructureGenerationConditionApi.init();
		StructurePlacementExclusionApi.init();
		StructureSetApi.init();
		TemplatePoolApi.init();
		PistonPushUtil.init();
		ScreenShakes.init();
		StructureStatusUpdater.init();

		FrozenLibNetworking.registerNetworking();

		RegistryFreezeEvents.START_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (allRegistries) ModIntegrations.initialize();
		});

		RegistryFreezeEvents.END_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (!allRegistries) return;
			for (BasicConfig<?> basicConfig : BasicConfigRegistry.getAllConfigs()) basicConfig.save();
		});

		FrozenLibConfig.CONFIG.load(true);
	}
}
