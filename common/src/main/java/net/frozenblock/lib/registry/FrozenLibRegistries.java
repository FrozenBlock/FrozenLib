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

package net.frozenblock.lib.registry;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroup;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.block.impl.sound.SoundTypeOverride;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateType;
import net.frozenblock.lib.integration.api.ModIntegration;
import net.frozenblock.lib.integration.api.ModIntegrationSupplier;
import net.frozenblock.lib.levelgen.material.impl.RuleSourceAddition;
import net.frozenblock.lib.music.api.structure.StructureMusic;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.platform.service.RegistryHelper;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundType;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

@UtilityClass
public class FrozenLibRegistries {
	public static final ResourceKey<Registry<ModIntegrationSupplier<?>>> MOD_INTEGRATION_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("mod_integration"));
	public static final MappedRegistry<ModIntegrationSupplier<?>> MOD_INTEGRATION = createSimple(MOD_INTEGRATION_REGISTRY, Lifecycle.stable(), false,
		registry -> Registry.register(registry, FrozenLibConstants.id("dummy"), new ModIntegrationSupplier<>(() -> new ModIntegration("dummy") {
			@Override
			public void init() {}
		},
		"dummy"
		))
	);

	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("sound_predicate"));
	public static final MappedRegistry<SoundPredicate<?>> SOUND_PREDICATE = createSimple(SOUND_PREDICATE_REGISTRY, Lifecycle.stable(), true,
		registry -> Registry.register(registry, FrozenLibConstants.id("dummy"), new SoundPredicate<>(() -> entity -> false))
	);

	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_UNSYNCED_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("sound_predicate_unsynced"));
	public static final MappedRegistry<SoundPredicate<?>> SOUND_PREDICATE_UNSYNCED = createSimple(SOUND_PREDICATE_UNSYNCED_REGISTRY, Lifecycle.stable(), false,
		registry -> Registry.register(registry, FrozenLibConstants.id("dummy"), new SoundPredicate<>(() -> entity -> false))
	);

	public static final ResourceKey<Registry<WindManagerExtensionType<?>>> WIND_MANAGER_EXTENSION_TYPE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("wind_manager_extension_type"));
	public static final MappedRegistry<WindManagerExtensionType<?>> WIND_MANAGER_EXTENSION_TYPE = createSimple(WIND_MANAGER_EXTENSION_TYPE_REGISTRY, Lifecycle.stable());

	public static final ResourceKey<Registry<WindDisturbanceType<?>>> WIND_DISTURBANCE_TYPE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("wind_disturbance_type"));
	public static final MappedRegistry<WindDisturbanceType<?>> WIND_DISTURBANCE_TYPE = createSimple(WIND_DISTURBANCE_TYPE_REGISTRY, Lifecycle.stable(), RegistryAttribute.SYNCED);

	public static final ResourceKey<Registry<ConfigPredicateType<?>>> CONFIG_PREDICATE_TYPE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("config_predicate_type"));
	public static final MappedRegistry<ConfigPredicateType<?>> CONFIG_PREDICATE_TYPE = createSimple(CONFIG_PREDICATE_TYPE_REGISTRY, Lifecycle.stable());

	public static final ResourceKey<Registry<MovingSoundType<?>>> MOVING_SOUND_TYPE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("moving_sound_type"));
	public static final MappedRegistry<MovingSoundType<?>> MOVING_SOUND_TYPE = createSimple(MOVING_SOUND_TYPE_REGISTRY, Lifecycle.stable());

	// DYNAMIC REGISTRIES
	public static final ResourceKey<Registry<SoundTypeOverride>> SOUND_TYPE_OVERRIDE = ResourceKey.createRegistryKey(FrozenLibConstants.id("sound_type_override"));
	public static final ResourceKey<Registry<StructureMusic>> STRUCTURE_MUSIC = ResourceKey.createRegistryKey(FrozenLibConstants.id("structure_music"));
	public static final ResourceKey<Registry<FireType>> FIRE_TYPE = ResourceKey.createRegistryKey(FrozenLibConstants.id("fire_type"));
	public static final ResourceKey<Registry<ClipGroup>> CLIP_GROUP = ResourceKey.createRegistryKey(FrozenLibConstants.id("clip_group"));
	public static final ResourceKey<Registry<WaterLikeType>> WATER_LIKE_TYPE = ResourceKey.createRegistryKey(FrozenLibConstants.id("water_like_type"));
	public static final ResourceKey<Registry<RuleSourceAddition>> RULE_SOURCE_ADDITION = ResourceKey.createRegistryKey(FrozenLibConstants.id("rule_source_addition"));

    public static void init() {
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(SOUND_TYPE_OVERRIDE, SoundTypeOverride.DIRECT_CODEC);
		SoundTypeOverrides.init();
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(STRUCTURE_MUSIC, StructureMusic.DIRECT_CODEC);
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(FIRE_TYPE, FireType.DIRECT_CODEC);
		FireData.init();
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(CLIP_GROUP, ClipGroup.DIRECT_CODEC);
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(WATER_LIKE_TYPE, WaterLikeType.DIRECT_CODEC);
		FrozenLibInitPlatformUtils.REGISTRY.registerSyncedDynamicRegistry(RULE_SOURCE_ADDITION, RuleSourceAddition.DIRECT_CODEC);
    }

	public static HolderLookup.Provider vanillaRegistries() {
		return VanillaRegistries.createLookup();
	}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return createSimple(key, lifecycle, false, null);
	}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean synced) {
		return createSimple(key, lifecycle, synced, null);
	}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean synced, RegistryHelper.RegistryBootstrap<T> bootstrap) {
		return FrozenLibInitPlatformUtils.REGISTRY.createSimpleRegistry(key, lifecycle, synced, bootstrap);
	}
}
