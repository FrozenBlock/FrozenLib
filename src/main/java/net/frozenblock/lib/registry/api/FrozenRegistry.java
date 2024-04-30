/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.registry.api;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.integration.api.ModIntegration;
import net.frozenblock.lib.integration.api.ModIntegrationSupplier;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class FrozenRegistry {
	private FrozenRegistry() {
		throw new UnsupportedOperationException("FrozenRegistry contains only static declarations.");
	}

	public static final ResourceKey<Registry<ModIntegrationSupplier<?>>> MOD_INTEGRATION_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("mod_integration"));
	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("sound_predicate"));
	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_UNSYNCED_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("sound_predicate_unsynced"));
	public static final ResourceKey<Registry<SpottingIconPredicate<?>>> SPOTTING_ICON_PREDICATE_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("spotting_icon_predicate"));
	public static final ResourceKey<Registry<WindDisturbanceLogic<?>>> WIND_DISTURBANCE_LOGIC_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("wind_disturbance_logic"));
	public static final ResourceKey<Registry<WindDisturbanceLogic<?>>> WIND_DISTURBANCE_LOGIC_UNSYNCED_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("wind_disturbance_logic_unsynced"));

	public static final MappedRegistry<ModIntegrationSupplier<?>> MOD_INTEGRATION = createSimple(MOD_INTEGRATION_REGISTRY, Lifecycle.stable(), null,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new ModIntegrationSupplier<>(() -> new ModIntegration("dummy") {
			@Override
			public void init() {}
		},
		"dummy"
		))
	);

	public static final MappedRegistry<SoundPredicate<?>> SOUND_PREDICATE = createSimple(SOUND_PREDICATE_REGISTRY, Lifecycle.stable(), RegistryAttribute.SYNCED,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new SoundPredicate<>(entity -> false))
	);

	public static final MappedRegistry<SoundPredicate<?>> SOUND_PREDICATE_UNSYNCED = createSimple(SOUND_PREDICATE_UNSYNCED_REGISTRY, Lifecycle.stable(), null,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new SoundPredicate<>(entity -> false))
	);

	public static final MappedRegistry<SpottingIconPredicate<?>> SPOTTING_ICON_PREDICATE = createSimple(SPOTTING_ICON_PREDICATE_REGISTRY, Lifecycle.stable(), RegistryAttribute.SYNCED,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new SpottingIconPredicate<>(entity -> false))
	);

	public static final MappedRegistry<WindDisturbanceLogic<?>> WIND_DISTURBANCE_LOGIC = createSimple(WIND_DISTURBANCE_LOGIC_REGISTRY, Lifecycle.stable(), RegistryAttribute.SYNCED,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new WindDisturbanceLogic(WindDisturbanceLogic.defaultPredicate()))
	);

	public static final MappedRegistry<WindDisturbanceLogic<?>> WIND_DISTURBANCE_LOGIC_UNSYNCED = createSimple(WIND_DISTURBANCE_LOGIC_UNSYNCED_REGISTRY, Lifecycle.stable(), null,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new WindDisturbanceLogic(WindDisturbanceLogic.defaultPredicate()))
	);

	@NotNull
	public static HolderLookup.Provider vanillaRegistries() {
		return VanillaRegistries.createLookup();
	}

    public static void initRegistry() {
    }

	private static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return createSimple(key, lifecycle, null);
	}

	private static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryAttribute attribute) {
		return createSimple(key, lifecycle, attribute, null);
	}

	private static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryAttribute attribute, BuiltInRegistries.RegistryBootstrap<T> bootstrap) {
		var registry = new MappedRegistry<>(key, lifecycle, false);
		var fabricRegistryBuilder = FabricRegistryBuilder.from(registry);

		if (attribute != null) {
			fabricRegistryBuilder.attribute(attribute);
		}

		var registeredRegistry = fabricRegistryBuilder.buildAndRegister();

		if (bootstrap != null) {
			bootstrap.run(registeredRegistry);
		}

		return registeredRegistry;
	}
}
