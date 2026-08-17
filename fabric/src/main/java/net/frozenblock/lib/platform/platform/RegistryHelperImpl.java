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
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.platform.FabricDeferredRegister;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public final class RegistryHelperImpl {

	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new FabricDeferredRegister<>(registryKey, namespace);
	}

	public static DeferredRegister.Items createDeferredItemsRegister(String namespace) {
		return new FabricDeferredRegister.Items(namespace);
	}

	public static DeferredRegister.Blocks createDeferredBlocksRegister(String namespace) {
		return new FabricDeferredRegister.Blocks(namespace);
	}

	public static DeferredRegister.DataComponents createDeferredDataComponentsRegister(String namespace) {
		return new FabricDeferredRegister.DataComponents(Registries.DATA_COMPONENT_TYPE, namespace);
	}

	public static DeferredRegister.Entities createDeferredEntitiesRegister(String namespace) {
		return new FabricDeferredRegister.Entities(namespace);
	}

	public static DeferredRegister.SoundEvents createDeferredSoundEventsRegister(String namespace) {
		return new FabricDeferredRegister.SoundEvents(namespace);
	}

	public static DeferredRegister.ParticleTypes createDeferredParticleTypesRegister(String namespace) {
		return new FabricDeferredRegister.ParticleTypes(namespace);
	}

	public static DeferredRegister.MemoryModuleTypes createDeferredMemoryModuleTypesRegister(String namespace) {
		return new FabricDeferredRegister.MemoryModuleTypes(namespace);
	}

	public static DeferredRegister.Activities createDeferredActivitiesRegister(String namespace) {
		return new FabricDeferredRegister.Activities(namespace);
	}

	public static DeferredRegister.SensorTypes createDeferredSensorTypesRegister(String namespace) {
		return new FabricDeferredRegister.SensorTypes(namespace);
	}

	public static <T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryHelper.RegistryBootstrap<T> bootstrap
	) {
		final var registry = new MappedRegistry<>(key, lifecycle, false);
		final var builder = FabricRegistryBuilder.from(registry);
		if (synced) builder.attribute(RegistryAttribute.SYNCED);
		final var registered = builder.buildAndRegister();
		if (bootstrap != null) bootstrap.run(registered);
		return registered;
	}

	public static <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		DynamicRegistries.register(key, directCodec);
	}

	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		DynamicRegistries.registerSynced(key, directCodec);
	}

	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec) {
		DynamicRegistries.registerSynced(key, directCodec, networkCodec);
	}
}
