/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;

public class FrozenRegistry {
	private FrozenRegistry() {
		throw new UnsupportedOperationException("FrozenRegistry contains only static declarations.");
	}

	public static final ResourceKey<Registry<ModIntegrationSupplier<?>>> MOD_INTEGRATION_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("mod_integration"));
	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("sound_predicate"));
	public static final ResourceKey<Registry<SoundPredicate<?>>> SOUND_PREDICATE_UNSYNCED_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("sound_predicate_unsynced"));
	public static final ResourceKey<Registry<SpottingIconPredicate<?>>> SPOTTING_ICON_PREDICATE_REGISTRY = ResourceKey.createRegistryKey(FrozenSharedConstants.id("spotting_icon_predicate"));


	public static final MappedRegistry<ModIntegrationSupplier<?>> MOD_INTEGRATION = createSimple(MOD_INTEGRATION_REGISTRY, Lifecycle.stable(), null,
		registry -> Registry.register(registry, FrozenSharedConstants.id("dummy"), new ModIntegrationSupplier<>(() -> new ModIntegration("dummy") {
			@Override
			public void init() {}
			},
				"dummy"
			)
		)
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
