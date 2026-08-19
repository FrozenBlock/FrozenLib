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

package net.frozenblock.lib.platform.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.registry.NeoDeferredRegister;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

public final class RegistryHelperImpl {
	private static final List<Registry<?>> PENDING_REGISTRIES = new ArrayList<>();
	private static final List<DynamicRegistryEntry<?>> PENDING_DYNAMIC_REGISTRIES = new ArrayList<>();

	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new NeoDeferredRegister<>(registryKey, namespace);
	}

	public static DeferredRegister.Items createDeferredItemsRegister(String namespace) {
		return new NeoDeferredRegister.Items(namespace);
	}

	public static DeferredRegister.Blocks createDeferredBlocksRegister(String namespace) {
		return new NeoDeferredRegister.Blocks(namespace);
	}

	public static DeferredRegister.DataComponents createDeferredDataComponentsRegister(String namespace) {
		return new NeoDeferredRegister.DataComponents(Registries.DATA_COMPONENT_TYPE, namespace);
	}

	public static DeferredRegister.Entities createDeferredEntitiesRegister(String namespace) {
		return new NeoDeferredRegister.Entities(namespace);
	}

	public static DeferredRegister.SoundEvents createDeferredSoundEventsRegister(String namespace) {
		return new NeoDeferredRegister.SoundEvents(namespace);
	}

	public static DeferredRegister.ParticleTypes createDeferredParticleTypesRegister(String namespace) {
		return new NeoDeferredRegister.ParticleTypes(namespace);
	}

	public static DeferredRegister.MemoryModuleTypes createDeferredMemoryModuleTypesRegister(String namespace) {
		return new NeoDeferredRegister.MemoryModuleTypes(namespace);
	}

	public static DeferredRegister.Activities createDeferredActivitiesRegister(String namespace) {
		return new NeoDeferredRegister.Activities(namespace);
	}

	public static DeferredRegister.SensorTypes createDeferredSensorTypesRegister(String namespace) {
		return new NeoDeferredRegister.SensorTypes(namespace);
	}

	public static DeferredRegister.PoiTypes createDeferredPoiTypesRegister(String namespace) {
		return new NeoDeferredRegister.PoiTypes(namespace);
	}

	public static <T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryHelper.RegistryBootstrap<T> bootstrap
	) {
		var builder = new RegistryBuilder<>(key).disableRegistrationCheck();
		if (synced) builder.sync(true);
		var registry = (MappedRegistry<T>) builder.create();
		if (bootstrap != null) bootstrap.run(registry);
		PENDING_REGISTRIES.add(registry);
		return registry;
	}

	public static <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		PENDING_DYNAMIC_REGISTRIES.add(new DynamicRegistryEntry<>(key, directCodec, null, false));
	}

	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		registerSyncedDynamicRegistry(key, directCodec, directCodec);
	}

	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec) {
		PENDING_DYNAMIC_REGISTRIES.add(new DynamicRegistryEntry<>(key, directCodec, networkCodec, true));
	}

	public static void flushRegistries(NewRegistryEvent event) {
		for (Registry<?> registry : PENDING_REGISTRIES) event.register(registry);
		PENDING_REGISTRIES.clear();
	}

	@SuppressWarnings("unchecked")
	public static void flushDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
		for (DynamicRegistryEntry<?> entry : PENDING_DYNAMIC_REGISTRIES) {
			final DynamicRegistryEntry typedEntry = entry;
			if (typedEntry.synced()) {
				event.dataPackRegistry(typedEntry.key(), typedEntry.codec(), typedEntry.networkCodec());
			} else {
				event.dataPackRegistry(typedEntry.key(), typedEntry.codec());
			}
		}
		PENDING_DYNAMIC_REGISTRIES.clear();
	}

	private record DynamicRegistryEntry<T>(ResourceKey<Registry<T>> key, Codec<T> codec, @Nullable Codec<T> networkCodec, boolean synced) {}
}
